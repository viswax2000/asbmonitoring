package com.vcc.asb.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.servicebus.AccessRights;
import com.microsoft.azure.management.servicebus.AuthorizationRule;
import com.microsoft.azure.management.servicebus.NamespaceAuthorizationRule;
import com.microsoft.azure.management.servicebus.Queue;
import com.microsoft.azure.management.servicebus.QueueAuthorizationRule;
import com.microsoft.azure.management.servicebus.QueueAuthorizationRules;
import com.microsoft.azure.management.servicebus.ServiceBusNamespace;
import com.microsoft.azure.management.servicebus.ServiceBusNamespaces;
import com.microsoft.azure.management.servicebus.ServiceBusSubscription;
import com.microsoft.azure.management.servicebus.Topic;
import com.microsoft.azure.management.servicebus.TopicAuthorizationRule;
import com.vcc.asb.config.model.EntityStatus;
import com.vcc.asb.config.model.MessageCountDetails;
import com.vcc.asb.config.model.NamespaceDescription;
import com.vcc.asb.config.model.NamespaceSku;
import com.vcc.asb.config.model.NamespaceType;
import com.vcc.asb.config.model.ObjectFactory;
import com.vcc.asb.config.model.QueueDescription;
import com.vcc.asb.config.model.Region;
import com.vcc.asb.config.model.SubscriptionDescription;
import com.vcc.asb.config.model.Tags;
import com.vcc.asb.config.model.TopicDescription;
import com.vcc.asb.configuration.ConfigConstants;
import com.vcc.asb.configuration.ServiceConfig;
import com.vcc.asb.util.NamespaceAuthRulesIterator;
import com.vcc.asb.util.NamespaceIterator;
import com.vcc.asb.util.QueueAuthRulesIterator;
import com.vcc.asb.util.QueueIterator;
import com.vcc.asb.util.SubscriptionIterator;
import com.vcc.asb.util.TopicAuthRulesIterator;
import com.vcc.asb.util.TopicIterator;

public class ASBEntities {
	
	List<NamespaceDescription> namespacesDesc;
	com.vcc.asb.config.model.ObjectFactory objFac = new com.vcc.asb.config.model.ObjectFactory();
	com.vcc.asb.metrics.model.ObjectFactory metricsObjFac = new com.vcc.asb.metrics.model.ObjectFactory();
	
	Map<String, List<String>> resourceGroupNamespaces;
	Map<String, List<ServiceBusNSAuthRules>> resGrpNSAuthRules;
	
	Map<String, List<QueueDescription>> namespaceQueues;
	Map<String, List<TopicDescription>> namespaceTopics;
	Map<String, List<SubscriptionDescription>> namespaceSubscriptions;
	
	private static Logger logger = LoggerFactory.getLogger(ASBEntities.class);
	
	public ASBEntities() {

		this.namespacesDesc = new ArrayList<NamespaceDescription>();
		this.resourceGroupNamespaces = new ConcurrentHashMap<String, List<String>>();
		this.resGrpNSAuthRules = new HashMap<String, List<ServiceBusNSAuthRules>>();
		
		this.namespaceQueues = new HashMap<String, List<QueueDescription>>();
		this.namespaceTopics = new HashMap<String, List<TopicDescription>>();
		this.namespaceSubscriptions = new HashMap<String, List<SubscriptionDescription>>();
		
	}
	
	public void populateNamespaces(Azure azure, ServiceConfig serviceConfig) {
		
		try {
			
			this.namespacesDesc = new ArrayList<NamespaceDescription>();
			ServiceBusNamespaces sbNamespaces = azure.serviceBusNamespaces();
			
			logger.info(">>> Number of namespaces:"+sbNamespaces.list().size());
			
			new NamespaceIterator() {
				
				@Override
				protected void handle(int nsIndex, ServiceBusNamespace ns) throws Exception {
					
					logger.info(">>>>>> Now processing for the Namespace :"+ns.name()+", resource group name:"+ns.resourceGroupName());
					
					NamespaceDescription nsDesc = objFac.createNamespaceDescription();
					populateNamespaceDescription(ns, nsDesc, objFac);
					groupNamespacesIntoResourceGroup(ns);
					namespacesDesc.add(nsDesc);
					nsDesc.setAuthorizationRules(objFac.createNamespaceDescriptionAuthorizationRules());

					ServiceBusNSAuthRules sbNSAuthRules = new ServiceBusNSAuthRules();
					sbNSAuthRules.setNamespaceName(ns.name());
					sbNSAuthRules.setResourceGroupName(ns.resourceGroupName());
					
					List<QueueDescription> nsQueues = new ArrayList<QueueDescription>();
					namespaceQueues.put(ns.name(), nsQueues);
					List<TopicDescription> nsTopics = new ArrayList<TopicDescription>();
					namespaceTopics.put(ns.name(), nsTopics);
					List<SubscriptionDescription> nsSubscriptions = new ArrayList<SubscriptionDescription>();
					namespaceSubscriptions.put(ns.name(), nsSubscriptions);
					
					new QueueIterator() {

						@Override
						protected void handle(int nsIndex, int queueIndex, ServiceBusNamespace ns, Queue queue) throws Exception {
							
							QueueDescription qDesc = objFac.createQueueDescription();
							QueueDescription.AuthorizationRules qDescAuthRules = objFac.createQueueDescriptionAuthorizationRules();
							populateQueueDescription(ns.name(), queue, qDesc, objFac, false);
							//nsDesc.getQueueDescriptions().add(qDesc);
							nsQueues.add(qDesc);

							List<com.vcc.asb.config.model.AuthorizationRule> authRulez = new ArrayList<com.vcc.asb.config.model.AuthorizationRule>();
							qDesc.setAuthorizationRules(qDescAuthRules);
							
							qDesc.getAuthorizationRules().getAuthorizationRules().addAll(authRulez);
							
							new QueueAuthRulesIterator() {
								
								protected void handle(int nsIndex, int queueIndex, int authRuleIndex, ServiceBusNamespace ns, Queue queue, 
										QueueAuthorizationRule queueAuthRule) throws Exception {
									
									com.vcc.asb.config.model.AuthorizationRule qAuthRuleD = objFac.createAuthorizationRule();
									populateQueueAuthorizationRule(queueAuthRule, qAuthRuleD, objFac);
									qDesc.getAuthorizationRules().getAuthorizationRules().add(qAuthRuleD);
									
									if(sbNSAuthRules.getQueueAuthRules().get(qAuthRuleD.getEntityName()) == null) {
										List<com.vcc.asb.config.model.AuthorizationRule> queueAuthRulesList = 
												new ArrayList<com.vcc.asb.config.model.AuthorizationRule>();
										queueAuthRulesList.add(qAuthRuleD);
										sbNSAuthRules.getQueueAuthRules().put(qAuthRuleD.getEntityName(), queueAuthRulesList);
									} else {
										List<com.vcc.asb.config.model.AuthorizationRule> queueAuthRulesList = 
												sbNSAuthRules.getQueueAuthRules().get(qAuthRuleD.getEntityName());
										queueAuthRulesList.add(qAuthRuleD);
									}
								}
								
							}.iterateThroughAuthRulesForQueue(ns, queue);
							
							logger.info("QueueDescription Authorization Rules:"+qDesc.getAuthorizationRules().getAuthorizationRules().size());

						}
						
					}.iterateThroughQueuesForNamespace(ns);



					new TopicIterator() {
						@Override
						protected void handle(int nsIndex, int topicIndex, ServiceBusNamespace ns, Topic topic)
								throws Exception {
							
							com.vcc.asb.config.model.TopicDescription topicDesc = objFac.createTopicDescription();
							TopicDescription.AuthorizationRules topicDescAuthRules = objFac.createTopicDescriptionAuthorizationRules();
							populateTopicDesciption(ns.name(), topic, topicDesc, objFac, false);
							//nsDesc.getTopicDescriptions().add(topicDesc);
							nsTopics.add(topicDesc);
							List<com.vcc.asb.config.model.AuthorizationRule> authRulez = new ArrayList<com.vcc.asb.config.model.AuthorizationRule>();
							topicDesc.setAuthorizationRules(topicDescAuthRules);
							
							//topicDesc.getAuthorizationRules().getAuthorizationRules().addAll(authRulez);
							topicDesc.setSubscriptionDescriptions(objFac.createTopicDescriptionSubscriptionDescriptions());
							
							new SubscriptionIterator() {
								
								@Override
								protected void handle(int nsIndex, int topicIndex, int subIndex, ServiceBusNamespace ns, Topic topic,
										ServiceBusSubscription sub) throws Exception {
									
									SubscriptionDescription subscDesc = objFac.createSubscriptionDescription();
									populateSubscriptionDescription(ns.name(), topic, sub, subscDesc, objFac);
									//topicDesc.getSubscriptionDescriptions().getSubscriptionDescriptions().add(subscDesc);
									nsSubscriptions.add(subscDesc);
									
								}
							}.iterateThroughSubscriptionsForTopic(ns, topic);
							
							new TopicAuthRulesIterator() {
								
								@Override
								protected void handle(int nsIndex, int topicIndex, int authRulesIndex, ServiceBusNamespace ns, Topic topic, 
										TopicAuthorizationRule topicAuthRule) throws Exception {
									
									com.vcc.asb.config.model.AuthorizationRule topicAuthRuleD = objFac.createAuthorizationRule();
									populateTopicAuthorizationRule(topicAuthRule, topicAuthRuleD, objFac);
									logger.info(" ======= Adding the Topic Auth Rules to the Topic ======= ");
									topicDesc.getAuthorizationRules().getAuthorizationRules().add(topicAuthRuleD);
									
									if(sbNSAuthRules.getTopicAuthRules().get(topicAuthRuleD.getEntityName()) == null) {
										List<com.vcc.asb.config.model.AuthorizationRule> topicAuthRulesList = 
												new ArrayList<com.vcc.asb.config.model.AuthorizationRule>();
										topicAuthRulesList.add(topicAuthRuleD);
										sbNSAuthRules.getTopicAuthRules().put(topicAuthRuleD.getEntityName(), topicAuthRulesList);
									} else {
										List<com.vcc.asb.config.model.AuthorizationRule> topicAuthRulesList =
												sbNSAuthRules.getTopicAuthRules().get(topicAuthRuleD.getEntityName());
										topicAuthRulesList.add(topicAuthRuleD);
									}
								}

							}.iterateThroughTopicAuthorizationRules(nsIndex, topicIndex, ns, topic);
							logger.info("TopicAuthRules Size:"+topicDesc.getAuthorizationRules().getAuthorizationRules().size());
						}
					}.iterateThroughTopicsForNamespace(ns);
					
					new NamespaceAuthRulesIterator() {

						@Override
						protected void handle(int nsIndex, int authRuleIndex, ServiceBusNamespace ns,
								NamespaceAuthorizationRule nsAuthRule) throws Exception {
							
							com.vcc.asb.config.model.AuthorizationRule nsAuthRuleD = objFac.createAuthorizationRule();
							populateAuthorizationRule(nsAuthRule, nsAuthRuleD, objFac);
							nsAuthRuleD.setLocation(nsAuthRule.inner().location());
							nsAuthRuleD.setNamespaceName(nsAuthRule.namespaceName());
							nsDesc.getAuthorizationRules().getAuthorizationRules().add(nsAuthRuleD);
							
							sbNSAuthRules.getNsAuthRules().add(nsAuthRuleD);
							if(nsAuthRule.name().equalsIgnoreCase(ConfigConstants.ROOT_MANAGE_SHARED_ACCESS_KEY)) {
								String resourceUri = ConfigConstants.SASTOKEN_NAMESPACE_RESOURCE_URI;
								resourceUri = resourceUri.replace("{namespaceName}", nsDesc.getNamespaceName());
								String sasToken = serviceConfig.getSASToken(resourceUri, ConfigConstants.ROOT_MANAGE_SHARED_ACCESS_KEY, nsAuthRule.getKeys().primaryKey());
								sbNSAuthRules.setNamespaceRootKeySASToken(sasToken);
							}
							
						}
					}.iterateThroughAuthRulesForNamespace(ns);
					
					logger.debug("Namespace Authorization Rules Size:"+nsDesc.getAuthorizationRules().getAuthorizationRules().size());
					
					if(resGrpNSAuthRules.get(sbNSAuthRules.getResourceGroupName()) == null) {
						List<ServiceBusNSAuthRules> resGrpNSAuthRulesList = new ArrayList<ServiceBusNSAuthRules>();
						resGrpNSAuthRulesList.add(sbNSAuthRules);
						resGrpNSAuthRules.put(sbNSAuthRules.getResourceGroupName(), resGrpNSAuthRulesList);
					} else {
						List<ServiceBusNSAuthRules> resGrpNSAuthRulesList = resGrpNSAuthRules.get(sbNSAuthRules.getResourceGroupName());
						resGrpNSAuthRulesList.add(sbNSAuthRules);
					}
					
				}
				
			}.iterateThroughNamespaces(sbNamespaces);
		
			logger.info(">>>>> Finished Iterating through all the objects ... ");
			
			//populateDefaultValues(this.namespacesDesc);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}

	public void populateNamespaceDescription(ServiceBusNamespace ns, NamespaceDescription nsD, ObjectFactory objFac) {
		
		logger.debug("Populating Namespace Description, namespace:"+ns.name()+", resource group name:"+ns.resourceGroupName());
		nsD.setDnsLabel(ns.dnsLabel());
		nsD.setFqdn(ns.fqdn());
		nsD.setName(ns.name());
		nsD.setId(ns.id());
		nsD.setKey(ns.key());
		nsD.setType(ns.type());
		nsD.setRegion(ns.regionName());
		nsD.setResourceGroup(ns.resourceGroupName());
		nsD.setCreatedAt(ns.createdAt());
		nsD.setUpdatedAt(ns.updatedAt());
		
		Region region = objFac.createRegion();
		region.setLabel(ns.region().label());
		region.setName(ns.region().name());
		
		nsD.setServiceBusEndpoint(ns.inner().serviceBusEndpoint());
		nsD.setTags(getTags(ns.inner().getTags()));
		
		if(ns.sku()!=null) {
			com.microsoft.azure.management.servicebus.NamespaceSku skuu = ns.sku();
			for(NamespaceSku sku: NamespaceSku.values()) {
				if(sku.name().toString().toUpperCase().startsWith(skuu.name().toString().toUpperCase())) {
					if(sku.name().toString().toUpperCase().startsWith("PREMIUM")) {
						if((sku.getCapacity() == skuu.capacity())) {
							nsD.setSku(sku);
							break;
						}
					} else {
						nsD.setSku(sku);
						break;
					}
				}
			}
		}
		
		
		nsD.setLocation(ns.inner().location());
		nsD.setProvisioningState(ns.inner().provisioningState());
		nsD.setMetricId(ns.inner().metricId());
		nsD.setNamespaceName(ns.name());
		nsD.setStatus(EntityStatus.ACTIVE);
		nsD.setMessagingSku((short)ns.sku().capacity());
		nsD.setCritical(true);
		nsD.setNamespaceType(NamespaceType.MESSAGING);
		
		nsD.setAuthorizationRules(objFac.createNamespaceDescriptionAuthorizationRules());
		
		for(NamespaceAuthorizationRule nsAuthRule: ns.authorizationRules().list()) {
			com.vcc.asb.config.model.AuthorizationRule authRule = objFac.createAuthorizationRule();
			authRule.setKeyName(nsAuthRule.name());
			authRule.setPrimaryKey(nsAuthRule.getKeys().primaryKey());
			authRule.setSecondaryKey(nsAuthRule.getKeys().secondaryKey());
			com.vcc.asb.config.model.Rights rights = objFac.createRights();
			authRule.setRights(rights);
			for(AccessRights ar: nsAuthRule.rights()) {
				rights.getAccessRights().add(ar.name());
			}
			authRule.setId(nsAuthRule.id());
			authRule.setKey(nsAuthRule.key());
			authRule.setNamespaceName(nsAuthRule.namespaceName());
			authRule.setResourceGroup(nsAuthRule.resourceGroupName());
			authRule.setRegion(nsAuthRule.region().name());
			nsD.getAuthorizationRules().getAuthorizationRules().add(authRule);
		}		
		
	}	
	
	public void populateQueueDescription(String namespace, Queue queue, QueueDescription queueD, 
			ObjectFactory objFac, boolean populateQueueAuthRules) throws DatatypeConfigurationException {
		
		logger.debug("Populating Queue Description for namespace:"+namespace+", Queue:"+queue.name()+", ResGrpName:"+queue.resourceGroupName());
		
		queueD.setId(queue.id());
		queueD.setKey(queue.key());
		queueD.setName(queue.name());
		queueD.setRegion(queue.regionName());
		queueD.setResourceGroup(queue.resourceGroupName());
		queueD.setLocation(queue.inner().location());
		queueD.setType(queue.type());
		queueD.setTags(getTags(queue.tags()));
		queueD.setCreatedAt(queue.createdAt());
		queueD.setUpdatedAt(queue.updatedAt());
		queueD.setAccessedAt(queue.accessedAt());
		
		
		MessageCountDetails countDetails = objFac.createMessageCountDetails();
		javax.xml.datatype.DatatypeFactory df = javax.xml.datatype.DatatypeFactory.newInstance();
		
		countDetails.setActiveMessageCount(queue.activeMessageCount());
		countDetails.setDeadLetterMessageCount(queue.deadLetterMessageCount());
		countDetails.setScheduledMessageCount(queue.scheduledMessageCount());
		countDetails.setTransferMessageCount(queue.transferMessageCount());
		countDetails.setTransferDeadLetterMessageCount(queue.transferDeadLetterMessageCount());
		
		//count
		queueD.setCountDetails(countDetails);
		queueD.setMessageCount(queue.activeMessageCount());
		queueD.setSizeInBytes(queue.currentSizeInBytes());
		queueD.setMaxSizeInMegabytes(queue.maxSizeInMB());
		queueD.setMessageCount(queue.messageCount());
		queueD.setMaxDeliveryCount(queue.maxDeliveryCountBeforeDeadLetteringMessage());
		
		logger.debug(">>>>>> Period:"+queue.defaultMessageTtlDuration());
		logger.debug(">>>>>> Period in Days:"+queue.defaultMessageTtlDuration().toStandardDays());
		logger.debug(">>>>>> Dftl TTL Days:"+queue.defaultMessageTtlDuration().toStandardDays().getDays());
		
		
		//duration
		if(queue.defaultMessageTtlDuration()!=null) {
			long queueDfltMsgTtlDays = queue.defaultMessageTtlDuration().toStandardDays().getDays();
			queueD.setDefaultMessageTimeToLive(df.newDuration(queueDfltMsgTtlDays * 24 * 60 * 60 * 1000l));
		}
		queueD.setLockDuration(df.newDuration(queue.lockDurationInSeconds() * 1000));
		queueD.setAutoDeleteOnIdle(df.newDuration(queue.deleteOnIdleDurationInMinutes() * 60 * 1000));
		
		if(queue.duplicateMessageDetectionHistoryDuration()!=null) {
			long queueDuplDetectHistDays = queue.duplicateMessageDetectionHistoryDuration().toStandardDays().getDays();
			queueD.setDuplicateDetectionHistoryTimeWindow(df.newDuration(queueDuplDetectHistDays * 24 * 60 * 60 * 1000));
		}
		
		//boolean
		queueD.setEnableBatchedOperations(queue.isBatchedOperationsEnabled());
		queueD.setDeadLetteringOnMessageExpiration(queue.isDeadLetteringEnabledForExpiredMessages());
		queueD.setRequiresSession(queue.isSessionEnabled());
		queueD.setRequiresDuplicateDetection(queue.isDuplicateDetectionEnabled());
		queueD.setSupportOrdering(queue.inner().supportOrdering()!=null?queue.inner().supportOrdering():Boolean.FALSE);
		queueD.setEnablePartitioning(queue.isPartitioningEnabled());
		queueD.setEnableExpress(queue.isExpressEnabled());
		queueD.setIsAnonymousAccessible(Boolean.FALSE);										//dont have the property in the returned Entity
		
		queueD.setStatus(com.vcc.asb.config.model.EntityStatus.valueOf(queue.status().name()));
		
				
		if(queue.status()!=null) {
			if(queue.status().name().equalsIgnoreCase("AVAILABLE")) {
				queueD.setEntityAvailabilityStatus(com.vcc.asb.config.model.EntityAvailabilityStatus.AVAILABLE);
			}
		}
			
		queueD.setNamespaceName(namespace);
		
		if(populateQueueAuthRules && queue.authorizationRules()!=null && queue.authorizationRules().list()!=null) {
			QueueAuthorizationRules qAuthRules = queue.authorizationRules();
			for(QueueAuthorizationRule qAuthRule: qAuthRules.list()) {
				com.vcc.asb.config.model.AuthorizationRule qAuthRuleD = objFac.createAuthorizationRule();
				populateQueueAuthorizationRule(qAuthRule, qAuthRuleD, objFac);
				queueD.getAuthorizationRules().getAuthorizationRules().add(qAuthRuleD);
			}			
		}
		
		
	}
	
	public void populateTopicDesciption(String namespace, Topic topic, TopicDescription topicD, 
			ObjectFactory objFac, boolean populateTopicAuthRules) throws DatatypeConfigurationException {
		
		logger.debug("Populating Topic Description for namespace:"+namespace+", topic:"+topic.name()+", ResGrpName:"+topic.resourceGroupName());
		
		//boolean
		topicD.setEnableBatchedOperations(topic.isBatchedOperationsEnabled());
		topicD.setRequiresDuplicateDetection(topic.isDuplicateDetectionEnabled());
		topicD.setEnableExpress(topic.isExpressEnabled());
		topicD.setEnablePartitioning(topic.isPartitioningEnabled());
		topicD.setSupportOrdering((topic.inner().supportOrdering()!=null)?topic.inner().supportOrdering():Boolean.FALSE);
		topicD.setFilteringMessagesBeforePublishing(Boolean.FALSE);			//dont have the property in the returned Entity
		topicD.setIsAnonymousAccessible(Boolean.FALSE);						//dont have the property in the returned Entity
		
		topicD.setSubscriptionCount(topic.subscriptionCount());
		topicD.setMaxSizeInMegabytes(topic.maxSizeInMB());
		
		javax.xml.datatype.DatatypeFactory df = javax.xml.datatype.DatatypeFactory.newInstance();
		MessageCountDetails countDetails = objFac.createMessageCountDetails();
		countDetails.setTransferMessageCount(topic.transferMessageCount());
		countDetails.setTransferDeadLetterMessageCount(topic.transferDeadLetterMessageCount());
		topicD.setSubscriptionCount(topic.subscriptionCount());
		countDetails.setScheduledMessageCount(topic.scheduledMessageCount());
		countDetails.setDeadLetterMessageCount(topic.deadLetterMessageCount());
		countDetails.setActiveMessageCount(topic.activeMessageCount());
		topicD.setCountDetails(countDetails);
		
		topicD.setMaxSizeInMegabytes(topic.maxSizeInMB());
		topicD.setSizeInBytes((topic.inner().sizeInBytes()!=null)?topic.inner().sizeInBytes():0);
		
		if(topic.defaultMessageTtlDuration()!=null) {
			long topicTtlDays = topic.defaultMessageTtlDuration().toStandardDays().getDays();
			topicD.setDefaultMessageTimeToLive(df.newDuration(topicTtlDays * 24 * 60 * 60 * 1000));
		}
		
		topicD.setAutoDeleteOnIdle(df.newDuration(topic.deleteOnIdleDurationInMinutes() * 60 * 1000));
		
		if(topic.duplicateMessageDetectionHistoryDuration()!=null) {
			long topicDuplDetecHistDays = topic.duplicateMessageDetectionHistoryDuration().toStandardDays().getDays();
			topicD.setDuplicateDetectionHistoryTimeWindow(df.newDuration(topicDuplDetecHistDays * 24 * 60 * 60 * 1000));
		}
		
		topicD.setAccessedAt(topic.accessedAt());
		topicD.setCreatedAt(topic.createdAt());
		topicD.setUpdatedAt(topic.updatedAt());
		
		topicD.setKey(topic.key());
		topicD.setName(topic.name());
		topicD.setLocation(topic.inner().location());
		topicD.setId(topic.id());
		topicD.setRegion(topic.regionName());
		topicD.setResourceGroup(topic.resourceGroupName());
		Region region = objFac.createRegion();
		region.setLabel(topic.region().label());
		region.setName(topic.region().name());
		topicD.setNamespaceName(namespace);
		
		if(populateTopicAuthRules) {
			com.vcc.asb.config.model.AuthorizationRule topicAuthRuleD = objFac.createAuthorizationRule();
			if(topic.authorizationRules()!=null && topic.authorizationRules().list()!=null && topic.authorizationRules().list().size()>0) {
				for(TopicAuthorizationRule topicAuthRule: topic.authorizationRules().list()) {
					populateTopicAuthorizationRule(topicAuthRule, topicAuthRuleD, objFac);
					topicD.getAuthorizationRules().getAuthorizationRules().add(topicAuthRuleD);
				}
			}
			
		}
	}
	
	public void populateSubscriptionDescription(String namespace, Topic topic, ServiceBusSubscription sub, 
			SubscriptionDescription subD, ObjectFactory objFac) throws DatatypeConfigurationException {
		
		logger.debug("Populating Subscription Description for namespace:"+namespace+", Subscription Name:"+sub.name()+
				", ResGrpName:"+sub.resourceGroupName());
		subD.setTopicName(topic.name());
		subD.setId(sub.id());
		subD.setName(sub.name());
		subD.setKey(sub.key());
		subD.setRegion(sub.regionName());
		subD.setResourceGroup(sub.resourceGroupName());
		subD.setLocation(sub.inner().location());
		subD.setStatus(EntityStatus.valueOf(sub.status().name()));
		subD.setType(sub.type());
		subD.setTags(getTags(sub.tags()));

		if(sub.status()!=null) {
			if(sub.status().name().equalsIgnoreCase("ACTIVE")) {
				subD.setEntityAvailabilityStatus(com.vcc.asb.config.model.EntityAvailabilityStatus.valueOf("AVAILABLE"));
			}
		}
		
		subD.setAccessedAt(sub.accessedAt());
		subD.setCreatedAt(sub.createdAt());
		subD.setUpdatedAt(sub.updatedAt());
		
		javax.xml.datatype.DatatypeFactory df = javax.xml.datatype.DatatypeFactory.newInstance();
		MessageCountDetails countDetails = objFac.createMessageCountDetails();
		
		countDetails.setActiveMessageCount(sub.activeMessageCount());
		countDetails.setDeadLetterMessageCount(sub.deadLetterMessageCount());
		countDetails.setTransferMessageCount(sub.transferMessageCount());
		countDetails.setTransferDeadLetterMessageCount(sub.transferDeadLetterMessageCount());
		countDetails.setScheduledMessageCount(sub.scheduledMessageCount());
		subD.setCountDetails(countDetails);
		subD.setMessageCount(sub.messageCount());
		
		if(sub.defaultMessageTtlDuration()!=null) {
			long days = sub.defaultMessageTtlDuration().toStandardDays().getDays();
			subD.setDefaultMessageTimeToLive(df.newDuration(days * 24 * 60 * 60 * 1000));
		}
		
		subD.setAutoDeleteOnIdle(df.newDuration(sub.deleteOnIdleDurationInMinutes() * 60 * 1000));

		subD.setLockDuration(df.newDuration(sub.lockDurationInSeconds()*1000));
		subD.setMaxDeliveryCount(sub.maxDeliveryCountBeforeDeadLetteringMessage());
		
		//boolean
		subD.setEnableBatchedOperations(sub.isBatchedOperationsEnabled());
		subD.setDeadLetteringOnMessageExpiration(sub.isDeadLetteringEnabledForExpiredMessages());
		subD.setDeadLetteringOnFilterEvaluationExceptions(sub.isDeadLetteringEnabledForFilterEvaluationFailedMessages());
		subD.setRequiresSession(sub.isSessionEnabled());
		
		subD.setNamespaceName(namespace);
		
	}
	
	public void populateAuthorizationRule(AuthorizationRule authRule, 
			com.vcc.asb.config.model.AuthorizationRule authRuleD, ObjectFactory objFac) {
		
		logger.debug("Populating Authorization Rule "+authRule.name()+", resource group:"+authRule.resourceGroupName());
		
		authRuleD.setId(authRule.id());
		authRuleD.setKey(authRule.key());
		authRuleD.setName(authRule.name());
		
		
		Region region = objFac.createRegion();
		region.setName(authRule.region().name());
		region.setLabel(authRule.region().label());
		
		authRuleD.setRegion(authRule.regionName());
		authRuleD.setResourceGroup(authRule.resourceGroupName());
		com.vcc.asb.config.model.Rights right =  objFac.createRights();
		authRuleD.setRights(right);
		
		List<com.microsoft.azure.management.servicebus.AccessRights> accessRights = authRule.rights();
		
		for(com.microsoft.azure.management.servicebus.AccessRights rights: accessRights) {
			authRuleD.getRights().getAccessRights().add(rights.name());
		}
		
		authRuleD.setTags(getTags(authRule.tags()));
		authRuleD.setType(authRule.type());
		
		authRuleD.setKeyName(authRule.name());
		authRuleD.setPrimaryKey(authRule.getKeys().primaryKey());
		authRuleD.setSecondaryKey(authRule.getKeys().secondaryKey());
		authRuleD.setPrimaryConnectionString(authRule.getKeys().primaryConnectionString());
		authRuleD.setSecondaryConnectionString(authRule.getKeys().secondaryConnectionString());
	
	}
	
	public void populateQueueAuthorizationRule(com.microsoft.azure.management.servicebus.QueueAuthorizationRule queueAuthRule, 
			com.vcc.asb.config.model.AuthorizationRule qAuthRuleD, ObjectFactory objFac) {
		
		logger.debug("Populating QueueAuthorizationRule queueName:"+queueAuthRule.queueName()+", namespace:"+queueAuthRule.namespaceName());
		populateAuthorizationRule(queueAuthRule, qAuthRuleD, objFac);
		
		qAuthRuleD.setEntityName(queueAuthRule.queueName());
		qAuthRuleD.setEntityType("QUEUE");
		qAuthRuleD.setNamespaceName(queueAuthRule.namespaceName());
		qAuthRuleD.setLocation(queueAuthRule.inner().location());

				
	}	
	
	public void populateTopicAuthorizationRule(com.microsoft.azure.management.servicebus.TopicAuthorizationRule topicAuthRule, 
			com.vcc.asb.config.model.AuthorizationRule topicAuthRuleD, ObjectFactory objFac) {
		
		logger.debug("Populating TopicAuthorizationRule queueName:"+topicAuthRule.topicName()+", namespace:"+topicAuthRule.namespaceName());
		
		populateAuthorizationRule(topicAuthRule, topicAuthRuleD, objFac);

		topicAuthRuleD.setEntityName(topicAuthRule.topicName());
		topicAuthRuleD.setEntityType("TOPIC");
		topicAuthRuleD.setNamespaceName(topicAuthRule.namespaceName());
		topicAuthRuleD.setLocation(topicAuthRule.inner().location());
				
	}

	
	public NamespaceDescription getNamespaceDescription(String namespace) {
		
		NamespaceDescription ns = null;
				
		for(NamespaceDescription nsDesc: namespacesDesc) {
			if(nsDesc.getNamespaceName().equalsIgnoreCase(namespace)) {
				ns = nsDesc;
				break;
			}
		}
		
		return ns;
	}
	
	public TopicDescription getTopicDescription(String namespace, String topicName) {
		//NamespaceDescription nsDesc = getNamespaceDescription(namespace);
		logger.debug("Getting the TopicDEscription for namespace:"+namespace+", topicName:"+topicName);
		
		List<TopicDescription> topicDescriptions = this.namespaceTopics.get(namespace);
		TopicDescription topicD = null;
		logger.debug("Got the TopicDescriptions size:"+topicDescriptions.size());
		
		for(TopicDescription topicDesc: topicDescriptions) {
			if(topicDesc.getName().equalsIgnoreCase(topicName)) {
				logger.debug("Got the TopicDescription successfully");
				topicD = topicDesc;
				break;
			}
		}
		
		logger.debug("Topic is null:"+(topicD==null));
		
		return topicD;
	}
	
	public QueueDescription getQueueDescription(String namespace, String queueName) {
		
		//NamespaceDescription nsDesc = getNamespaceDescription(namespace);
		List<QueueDescription> queueDescriptions = namespaceQueues.get(namespace);
		QueueDescription queueD = null;
		
		for(QueueDescription qDesc: queueDescriptions) {
			if(qDesc.getName().equalsIgnoreCase(queueName)) {
				queueD = qDesc;
				break;
			}
		}

		return queueD;
	}
	
	public SubscriptionDescription getSubscriptionDescription(String namespace, String topicName, String subName) {
		
		TopicDescription topicD = getTopicDescription(namespace, topicName);
		
		SubscriptionDescription subD = null;
		
		for(SubscriptionDescription subDesc: topicD.getSubscriptionDescriptions().getSubscriptionDescriptions()) {
			if(subDesc.getName().equalsIgnoreCase(subName)) {
				subD = subDesc;
				break;
			}
		}
		
		return subD;
	}
	
	private void groupNamespacesIntoResourceGroup(ServiceBusNamespace ns) {
		
		logger.debug("Populating Namespaces into Resource Groups");
		String namespaceName = ns.name();
		String resGroupName = ns.resourceGroupName();
		
		if(this.resourceGroupNamespaces.get(resGroupName) == null) {
			List<String> namespaces = new ArrayList<String>();
			namespaces.add(namespaceName);
			this.resourceGroupNamespaces.put(resGroupName, namespaces);
			
		} else {
			List<String> namespaces = this.resourceGroupNamespaces.get(resGroupName);
			if(namespaces.contains(namespaceName) == false) {
				namespaces.add(namespaceName);
			}
			
		}
		
	}
	
	public Map<String, List<ServiceBusNSAuthRules>> getResourceGroupNSAuthRules() {
		return this.resGrpNSAuthRules;
	}
	
	public ServiceBusNSAuthRules getNSAuthRules(String resourceGroupName, String namespaceName) {
		ServiceBusNSAuthRules sbNSAuthRules = null;
		
		outer:for(Entry<String, List<ServiceBusNSAuthRules>> sbNS: this.resGrpNSAuthRules.entrySet()) {
			if(sbNS.getKey().equalsIgnoreCase(resourceGroupName)) {
				for(ServiceBusNSAuthRules authRules: sbNS.getValue()) {
					if(authRules.getNamespaceName().equalsIgnoreCase(namespaceName)) {
						sbNSAuthRules = authRules;
						break outer;
					}
				}
			}
		}
		
		return sbNSAuthRules;
	}
	
	public List<NamespaceDescription> getAllNamespaceDescriptions() {
		return this.namespacesDesc;
	}
	
	private Tags getTags(Map<String,String> tags) {
		Tags tt = objFac.createTags();
		
		List<Tags.Entry> tagEntries = tt.getEntries();
		
		for(Entry<String,String> entry: tags.entrySet()) {
			Tags.Entry ttEntry = new Tags.Entry();
			ttEntry.setName(entry.getKey());
			ttEntry.setValue(entry.getValue());
			tagEntries.add(ttEntry);
		}
		
		return tt;
	}
	
	public List<com.vcc.asb.model.Namespace> getNamespaces() {
		
		List<Namespace> namesapces = new ArrayList<Namespace>();
		
		for(NamespaceDescription nsDesc: this.namespacesDesc) {
			
			Namespace ns = new Namespace(nsDesc.getResourceGroup(), nsDesc.getNamespaceName());
			
			ServiceBusNSAuthRules sbNSAuthRule = getNSAuthRules(nsDesc.getResourceGroup(), nsDesc.getNamespaceName());
			
			for(com.vcc.asb.config.model.AuthorizationRule authRule: nsDesc.getAuthorizationRules().getAuthorizationRules()) {
				AuthKeys authKeys = new AuthKeys(authRule.getKeyName(), authRule.getPrimaryKey(), authRule.getSecondaryKey());
				ns.getAuthKeys().add(authKeys);
			}
			
			ns.setSasTokenNamespaceRootKey(sbNSAuthRule.getNamespaceRootKeySASToken());

			namesapces.add(ns);

		}

		return namesapces;
	}
	
	public List<com.vcc.asb.model.Queue> getQueues() {

		List<com.vcc.asb.model.Queue> queues = new ArrayList<com.vcc.asb.model.Queue>();
		
		for(List<QueueDescription> qdList: this.namespaceQueues.values()) {
			for(QueueDescription qd: qdList) {

				com.vcc.asb.model.Queue queue = new com.vcc.asb.model.Queue(qd.getResourceGroup(), qd.getNamespaceName(), qd.getName());

				for(com.vcc.asb.config.model.AuthorizationRule authRule: qd.getAuthorizationRules().getAuthorizationRules()) {
					AuthKeys authKeys = new AuthKeys(authRule.getKeyName(), authRule.getPrimaryKey(), authRule.getSecondaryKey());
					queue.getAuthKeys().add(authKeys);
				}
				
				ServiceBusNSAuthRules sbNSAuthRule = getNSAuthRules(qd.getResourceGroup(), qd.getNamespaceName());
				for(com.vcc.asb.config.model.AuthorizationRule nsAuthRule: sbNSAuthRule.getNsAuthRules()) {
					if(nsAuthRule.getKeyName().equalsIgnoreCase("RootManageSharedAccessKey")) {
						AuthKeys authKeys = new AuthKeys(nsAuthRule.getKeyName(), nsAuthRule.getPrimaryKey(), nsAuthRule.getSecondaryKey());
						queue.getAuthKeys().add(authKeys);
						break;
					}
				}
				
				queue.setSasTokenNamespaceRootKey(sbNSAuthRule.getNamespaceRootKeySASToken());

				queues.add(queue);

			}
		}

		return queues;
	}
	
	public List<com.vcc.asb.model.Topic> getTopics() {
		
		List<com.vcc.asb.model.Topic> topics = new ArrayList<com.vcc.asb.model.Topic>();
		
		for(List<TopicDescription> topicDescList: this.namespaceTopics.values()) {
			
			for(TopicDescription td: topicDescList) {
				
				com.vcc.asb.model.Topic topic = new com.vcc.asb.model.Topic(td.getResourceGroup(), td.getNamespaceName(), td.getName());
				
				for(com.vcc.asb.config.model.AuthorizationRule authRule: td.getAuthorizationRules().getAuthorizationRules()) {
					AuthKeys authKeys = new AuthKeys(authRule.getKeyName(), authRule.getPrimaryKey(), authRule.getSecondaryKey());
					topic.getAuthKeys().add(authKeys);
				}
				
				ServiceBusNSAuthRules sbNSAuthRule = getNSAuthRules(td.getResourceGroup(), td.getNamespaceName());
				
				for(com.vcc.asb.config.model.AuthorizationRule nsAuthRule: sbNSAuthRule.getNsAuthRules()) {
					
					if(nsAuthRule.getKeyName().equalsIgnoreCase("RootManageSharedAccessKey")) {
						
						AuthKeys authKeys = new AuthKeys(nsAuthRule.getKeyName(), nsAuthRule.getPrimaryKey(), nsAuthRule.getSecondaryKey());
						topic.getAuthKeys().add(authKeys);
						break;
					}
				}
				
				topic.setSasTokenNamespaceRootKey(sbNSAuthRule.getNamespaceRootKeySASToken());
				
				topics.add(topic);
			}
		}
		return topics;
	}
	
	public List<com.vcc.asb.model.Subscription> getSubscriptions() {
		
		List<com.vcc.asb.model.Subscription> subscriptions = new ArrayList<com.vcc.asb.model.Subscription>();
		
		for(List<SubscriptionDescription> subList: this.namespaceSubscriptions.values()) {
			for(SubscriptionDescription subD: subList) {
				
				com.vcc.asb.model.Subscription subscription = new com.vcc.asb.model.Subscription(
							subD.getResourceGroup(), subD.getNamespaceName(), subD.getTopicName(), subD.getName());
				
				logger.debug("=== Getting the Topic for Namespace:"+subD.getNamespaceName()+", TopicName:"+subD.getTopicName());
				TopicDescription topicD = getTopicDescription(subD.getNamespaceName(), subD.getTopicName());
				logger.debug("Got the TopicDescription, (topicD==null)?"+(topicD == null));
				
				for(com.vcc.asb.config.model.AuthorizationRule authRuleD: topicD.getAuthorizationRules().getAuthorizationRules()) {
					com.vcc.asb.model.AuthKeys authKeys = new com.vcc.asb.model.AuthKeys(authRuleD.getKeyName(), authRuleD.getPrimaryKey(), authRuleD.getSecondaryKey());
					subscription.getAuthKeys().add(authKeys);
				}
				
				ServiceBusNSAuthRules sbNSAuthRule = getNSAuthRules(topicD.getResourceGroup(), topicD.getNamespaceName());
				
				for(com.vcc.asb.config.model.AuthorizationRule nsAuthRule: sbNSAuthRule.getNsAuthRules()) {
					if(nsAuthRule.getKeyName().equalsIgnoreCase("RootManageSharedAccessKey")) {
						AuthKeys authKeys = new AuthKeys(nsAuthRule.getKeyName(), nsAuthRule.getPrimaryKey(), nsAuthRule.getSecondaryKey());
						subscription.getAuthKeys().add(authKeys);
						break;
					}
				}
				
				subscription.setSasTokenNamespaceRootKey(sbNSAuthRule.getNamespaceRootKeySASToken());
				
				subscriptions.add(subscription);
			}
		}
		
		return subscriptions;

	}
	
	public com.vcc.asb.config.model.ObjectFactory getConfigObjectFactory() {
		return objFac;
	}
	
	public com.vcc.asb.metrics.model.ObjectFactory getMetricsObjectFactory() {
		return metricsObjFac;
	}	
	
	

}
