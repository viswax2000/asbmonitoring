package com.vcc.asb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.servicebus.Queue;
import com.microsoft.azure.management.servicebus.Queues;
import com.microsoft.azure.management.servicebus.ServiceBusNamespace;
import com.microsoft.azure.management.servicebus.ServiceBusNamespaces;
import com.microsoft.azure.management.servicebus.ServiceBusSubscription;
import com.microsoft.azure.management.servicebus.Topic;
import com.microsoft.azure.management.servicebus.Topics;
import com.vcc.asb.config.model.NamespaceDescription;
import com.vcc.asb.configuration.EntityType;
import com.vcc.asb.context.AppContext;

/**
 * Azure management client accessing the Azure Management API endpoints through the libraries.
 * Requires the ServicePrincipal to be configured to access the Azure Service Management API
 * 
 * @author Volvo-IT/HCL
 *
 */
@Service
public class AzureServiceClient extends BaseClient {
	
	private static Logger logger = LoggerFactory.getLogger(AzureServiceClient.class);

	//We should not get in here, do nothing if incase .. we dont handle metrics with AzureServiceClient.
	@Override
	public <T> boolean executeMetricsCommand(ClientRequest<T> clRequest) {
		
		return false;
		
	}
	
	/**
	 * Fetches the Configuration information for Namespace,Queue,Topic,Subscription through the
	 * Azure Java libraries with underlying invokation of the Azure Management API
	 * 
	 */
	@Override
	public <T> boolean executeConfigCommand(ClientRequest<T> clRequest) {
		
		boolean isSuccess = false;
		Exception ex = null;
		
		Azure azureService = getAzureService();
		logger.debug("Executing the ConfigCommand @ AzureServiceClient, Entity:"+clRequest.getEntityName()+",EntityType:"+clRequest.getEntityType().name());

		ServiceBusNamespaces namespaces = azureService.serviceBusNamespaces();
		ServiceBusNamespace namespace = namespaces.getByResourceGroup(clRequest.getResourceGroupName(), clRequest.getEntityName());
		
		try {
		
			if((clRequest.getEntityType() == EntityType.NAMESPACE) && 
					(clRequest.getEntityName().equalsIgnoreCase(clRequest.getNamespaceName()))) {
				
				//so we retrieve the ServiceBusNamespace from Azure service
				//and populate it to NamespaceDescription
				NamespaceDescription nsD = AppContext.getConfigObjectFactory().createNamespaceDescription();
				AppContext.getAsbEntities().populateNamespaceDescription(namespace, nsD, AppContext.getConfigObjectFactory());
				logger.debug("Populated NamespaceDescription @ AzureServiceClient Successfully");
				clRequest.setResponse((T)nsD);
				
			} else if(clRequest.getEntityType() == EntityType.QUEUE){
				
				Queues queues = namespace.queues();
				Queue queue = queues.getByName(clRequest.getEntityName());
				com.vcc.asb.config.model.QueueDescription qd = AppContext.getConfigObjectFactory().createQueueDescription();
				AppContext.getAsbEntities().populateQueueDescription(clRequest.getNamespaceName(), queue, qd, 
						AppContext.getConfigObjectFactory(), true);
				logger.debug("Populated QueueDescription @ AzureServiceClient Successfully");
				clRequest.setResponse((T)qd);
				
			} else if(clRequest.getEntityType() == EntityType.TOPIC){
				
				Topics topics = namespace.topics();
				Topic topic = topics.getByName(clRequest.getEntityName());
				com.vcc.asb.config.model.TopicDescription topicD = AppContext.getConfigObjectFactory().createTopicDescription();
				AppContext.getAsbEntities().populateTopicDesciption(clRequest.getNamespaceName(), topic, topicD, AppContext.getConfigObjectFactory(), true);
				logger.debug("Populated TopicDescription @ AzureServiceClient Successfully");
				clRequest.setResponse((T)topicD);
				
			} else if(clRequest.getEntityType() == EntityType.SUBSCRIPTION){
				
				Topics topics = namespace.topics();
				Topic topic = topics.getByName(clRequest.getParentTopicName());
				ServiceBusSubscription sub = null;
				
				com.vcc.asb.config.model.SubscriptionDescription subD = AppContext.getConfigObjectFactory().createSubscriptionDescription();
				
				for(ServiceBusSubscription sbSub: topic.subscriptions().list()) {
					if(sbSub.name().equalsIgnoreCase(clRequest.getEntityName())) {
						sub = sbSub;
						break;
					}
				}
				
				AppContext.getAsbEntities().populateSubscriptionDescription(clRequest.getNamespaceName(), topic, sub, subD, AppContext.getConfigObjectFactory());
				logger.debug("Populated SubscriptionDescription @ AzureServiceClient Successfully");
				
				clRequest.setResponse((T)subD);
				
			}
			
			isSuccess = true;
		
		} catch(Exception e) {
			isSuccess = false;
			ex = e;
			logger.error("Exception caught while Executing ConfigCommand @ AzureServiceClient", e);
		}
		
		if(ex!=null) {
			throw new RuntimeException(ex);
		}

		return isSuccess;
	}

	@Override
	public RestTemplate getRestTemplate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void releaseRestTemplate(RestTemplate rt) {
		// TODO Auto-generated method stub
		
	}
}
