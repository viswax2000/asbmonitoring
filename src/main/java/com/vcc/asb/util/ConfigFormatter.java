package com.vcc.asb.util;

import java.io.StringWriter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.vcc.asb.config.model.AccessRights;
import com.vcc.asb.config.model.AuthorizationRule;
import com.vcc.asb.config.model.NamespaceDescription;
import com.vcc.asb.config.model.QueueDescription;
import com.vcc.asb.config.model.RuleDescription;
import com.vcc.asb.config.model.SubscriptionDescription;
import com.vcc.asb.config.model.TopicDescription;

@Component
public class ConfigFormatter {
	
	public static final String TAB = "\t";
	public static final String NEWLINE = "\n";
	public static final String YES = "Yes";
	public static final String NO = "No";
	
	public String processNamespaceDescriptions(List<com.vcc.asb.config.model.NamespaceDescription> namespaces) {
		
		StringWriter nsWriter = new StringWriter();
		StringWriter queueWriter = new StringWriter();
		StringWriter topicWriter = new StringWriter();
		StringWriter subWriter = new StringWriter();
		StringWriter filterWriter = new StringWriter();
		StringWriter sw = new StringWriter();
		StringWriter authRulesWriter = new StringWriter();
		
		//sw.write("<Namespaces>");
		
		for(NamespaceDescription ns: namespaces) {
			
			String nsXml = processNamespace(ns);
			nsWriter.write(nsXml+NEWLINE);
			String nsAuthRules = processAuthRules(ns.getAuthorizationRules().getAuthorizationRules());
			authRulesWriter.write(nsAuthRules);
			
			if(ns.getQueueDescriptions()!=null && ns.getQueueDescriptions().getQueueDescriptions()!=null) {
				for(QueueDescription qd: ns.getQueueDescriptions().getQueueDescriptions()) {
					String queueXml = processQueueDescription(qd);
					queueWriter.write(queueXml+NEWLINE);
					String qAuthRules = processAuthRules(qd.getAuthorizationRules().getAuthorizationRules());
					authRulesWriter.write(qAuthRules);
				}
			}
			

			if(ns.getTopicDescriptions()!=null && ns.getTopicDescriptions().getTopicDescriptions()!=null) {
				for(TopicDescription topicD: ns.getTopicDescriptions().getTopicDescriptions()) {
					String topicXml = processTopicDescription(topicD);
					topicWriter.write(topicXml+NEWLINE);
					String topicAuthRules = processAuthRules(topicD.getAuthorizationRules().getAuthorizationRules());
					authRulesWriter.write(topicAuthRules);
					
					for(SubscriptionDescription subD: topicD.getSubscriptionDescriptions().getSubscriptionDescriptions()) {
						String subXml = processSubscriptionDescription(subD);
						subWriter.write(subXml+NEWLINE);
	
						if(subD.getDefaultRuleDescription()!=null) {
	
							StringBuilder filterSB = new StringBuilder();
							RuleDescription ruleD = subD.getDefaultRuleDescription();
							filterSB.append(ruleD.getName()).append(TAB).append(subD.getNamespaceName()).append(TAB)
									.append(topicD.getName()).append(TAB).append(subD.getName())
									.append(ruleD.getAction().toString()).append(TAB)
									.append(ruleD.getFilter().toString());
							filterWriter.write(filterSB.toString());
	
						}
					}
				}
			}
		
		}
		
		sw.write("AzureSBNamespace\n");
		sw.write(nsWriter.toString());
		
		/*sw.write("\nAzureSBQueue\n");
		sw.write(queueWriter.toString());
		
		sw.write("\nAzureSBTopic\n");
		sw.write(topicWriter.toString());
		
		sw.write("\nAzureSBSubscription\n");
		sw.write(subWriter.toString());
		
		sw.write("\nAzureSBFilter\n");
		sw.write(filterWriter.toString());*/
		
		sw.write("\nAzureSBAuthorizationRules\n");
		sw.write(authRulesWriter.toString());
		
		return sw.toString();
		
	}
	
	public String processQueueDescriptions(List<QueueDescription> queues) {
		
		StringWriter sw = new StringWriter();
		sw.write("\nAzureSBQueue\n");
		for(QueueDescription queueD: queues) {
			String queueOutput = processQueueDescription(queueD);
			sw.write(queueOutput+NEWLINE);
		}
		
		return sw.toString();
	}
	
	public String processTopicDescriptions(List<TopicDescription> topics) {
		StringWriter sw = new StringWriter();
		sw.write("\nAzureSBTopic\n");
		for(TopicDescription topicD: topics) {
			String topicOutput = processTopicDescription(topicD);
			sw.write(topicOutput+NEWLINE);
		}
		
		return sw.toString();
	}
	
	public String processSubscriptionDescriptions(List<SubscriptionDescription> subscriptions) {
		StringWriter sw = new StringWriter();
		sw.write("\nAzureSBSubscription\n");
		StringWriter filterWriter = new StringWriter();
		filterWriter.write("\nAzureSBFilter\n");
		
		for(SubscriptionDescription subD: subscriptions) {
			String subscriptionOutput = processSubscriptionDescription(subD);
			sw.write(subscriptionOutput+NEWLINE);
			
			if(subD.getDefaultRuleDescription()!=null) {

				StringBuilder filterSB = new StringBuilder();
				RuleDescription ruleD = subD.getDefaultRuleDescription();
				
				filterSB.append(ruleD.getName()).append(TAB).append(subD.getNamespaceName()).append(TAB)
						.append(subD.getTopicName()).append(TAB).append(subD.getName())
						.append(ruleD.getAction().toString()).append(TAB)
						.append(ruleD.getFilter().toString());
				filterWriter.write(filterSB.toString());

			}			
		}
		
		return sw.toString() + filterWriter.toString();
	}
	
	
	
	public String processNamespace(NamespaceDescription ns) {
		StringBuilder sb = new StringBuilder();
		sb.append(ns.getNamespaceName());
		return sb.toString();
	}
	
	public String processQueueDescription(QueueDescription queue) {
		
		long lockDuration = 0;
		if(queue.getLockDuration()!=null) {
			lockDuration = queue.getLockDuration().getTimeInMillis(new java.util.Date());			//Millis
			lockDuration = lockDuration / 60;														//Seconds
		}
		
		long duplDetectHistTimeWindow = 0;
		if(queue.getDuplicateDetectionHistoryTimeWindow()!=null) {
			duplDetectHistTimeWindow = (queue.getDuplicateDetectionHistoryTimeWindow().getTimeInMillis(new java.util.Date()) / 60);
		}
		
		long dftMsgTimeToLive = 0;
		if(queue.getDefaultMessageTimeToLive()!=null) {
			dftMsgTimeToLive = (queue.getDefaultMessageTimeToLive().getTimeInMillis(new java.util.Date()) / 60);
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(queue.getName()).append(TAB).append(queue.getNamespaceName())
		   .append(TAB)
		   .append(queue.getAutoDeleteOnIdle()!=null?YES:NO)
		   .append(TAB)
		   .append(dftMsgTimeToLive)
		   .append(TAB)
		   .append(duplDetectHistTimeWindow)
		   .append(TAB)
		   .append(queue.isEnableBatchedOperations()?YES:NO)
		   .append(TAB)
		   .append(queue.isDeadLetteringOnMessageExpiration()?YES:NO)
		   .append(TAB)
		   .append(queue.isEnableExpress()?YES:NO)
		   .append(TAB)
		   .append(queue.isEnablePartitioning()?YES:NO)
		   .append(TAB)
		   .append(queue.getForwardTo())
		   .append(TAB)
		   .append(queue.isIsAnonymousAccessible()?YES:NO)
		   .append(TAB)
		   .append(lockDuration)
		   .append(TAB)
		   .append(queue.getMaxDeliveryCount())
		   .append(TAB)
		   .append(queue.getMaxSizeInMegabytes())
		   .append(TAB)
		   .append(queue.isRequiresDuplicateDetection()?YES:NO)
		   .append(TAB)
		   .append(queue.isRequiresSession()?YES:NO)
		   .append(TAB)
		   .append(queue.isSupportOrdering()?YES:NO);
		
		return sb.toString();
	}
	
	public String processTopicDescription(TopicDescription topicD) {
		
		long duplDetectHistTimeWindow = 0;
		long dfltMessageTimeToLive = 0;
		
		if(topicD.getDuplicateDetectionHistoryTimeWindow()!=null) {
			duplDetectHistTimeWindow = (topicD.getDuplicateDetectionHistoryTimeWindow().getTimeInMillis(new java.util.Date()) / 60);
		}
		if(topicD.getDefaultMessageTimeToLive()!=null) {
			dfltMessageTimeToLive = (topicD.getDefaultMessageTimeToLive().getTimeInMillis(new java.util.Date()) / 60);
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(topicD.getName()).append(TAB)
		  .append(topicD.getNamespaceName()).append(TAB)
		  .append(topicD.isRequiresDuplicateDetection()?YES:NO).append(TAB)
		  .append(topicD.isSupportOrdering()?YES:NO).append(TAB)
		  .append(topicD.getMaxSizeInMegabytes()).append(TAB)
		  .append(topicD.isIsAnonymousAccessible()?YES:NO).append(TAB)
		  .append(topicD.isEnablePartitioning()?YES:NO).append(TAB)
		  .append(topicD.isFilteringMessagesBeforePublishing()?YES:NO).append(TAB)
		  .append(topicD.isEnableExpress()?YES:NO).append(TAB)
		  .append(topicD.isEnableBatchedOperations()?YES:NO).append(TAB)
		  .append(duplDetectHistTimeWindow).append(TAB)
		  .append(dfltMessageTimeToLive).append(TAB)
		  .append(topicD.getDefaultMessageTimeToLive()!=null?YES:NO);
		
		return sb.toString();
		  
		
	}
	
	public String processSubscriptionDescription(SubscriptionDescription subD) {
		
		long lockDuration = 0;
		long dfltMsgTimeToLive = 0;
		
		if(subD.getLockDuration()!=null) {
			lockDuration = (subD.getLockDuration().getTimeInMillis(new java.util.Date()) / 60);
		}
		if(subD.getDefaultMessageTimeToLive()!=null) {
			dfltMsgTimeToLive = (subD.getDefaultMessageTimeToLive().getTimeInMillis(new java.util.Date()) / 60);
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(subD.getName()).append(TAB)
		  .append(subD.getNamespaceName()).append(TAB)
		  //.append(subD.getTopicName()).append(TAB)
		  .append(subD.isRequiresSession()?YES:NO).append(TAB)
		  .append(subD.getMaxDeliveryCount()).append(TAB)
		  .append(lockDuration).append(TAB)
		  .append(subD.getForwardTo()).append(TAB)
		  .append(subD.isDeadLetteringOnMessageExpiration()?YES:NO).append(TAB)
		  .append(subD.isDeadLetteringOnFilterEvaluationExceptions()?YES:NO).append(TAB)
		  .append(subD.isEnableBatchedOperations()?YES:NO).append(TAB)
		  .append(dfltMsgTimeToLive).append(TAB)
		  .append(subD.getAutoDeleteOnIdle()!=null?NO:YES);
		
		return sb.toString();
	}
	
	public String processAuthRules(List<AuthorizationRule> authRules) {
		
		StringWriter authRulesWriter = new StringWriter();
		
		for(AuthorizationRule authRule: authRules) {
			
			StringBuilder sb = new StringBuilder();
			
			sb.append(authRule.getKeyName()).append(TAB)
			  .append(authRule.getNamespaceName()).append(TAB)
			  .append(authRule.getEntityName()).append(TAB)
			  .append(authRule.getEntityType()).append(TAB);
			
			if(authRule.getRights()!=null && authRule.getRights().getAccessRights()!=null && 
					authRule.getRights().getAccessRights().size()>0) {
				if(authRule.getRights().getAccessRights().contains(AccessRights.MANAGE.value())) {
					sb.append(YES).append(TAB);
				} else {
					sb.append(NO).append(TAB);
				}
				if(authRule.getRights().getAccessRights().contains(AccessRights.SEND.value())) {
					sb.append(YES).append(TAB);
				} else {
					sb.append(NO).append(TAB);
				}
				if(authRule.getRights().getAccessRights().contains(AccessRights.LISTEN.name())) {
					sb.append(YES).append(TAB);
				} else {
					sb.append(NO).append(TAB);
				}
			}
			
			sb.append(authRule.getPrimaryKey()).append(TAB);
			sb.append(authRule.getSecondaryKey());
			
			authRulesWriter.write(sb.toString()+NEWLINE);
		}
		return authRulesWriter.toString();
	}

}
