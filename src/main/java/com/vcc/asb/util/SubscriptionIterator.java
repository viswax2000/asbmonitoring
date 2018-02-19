package com.vcc.asb.util;

import com.microsoft.azure.management.servicebus.ServiceBusNamespace;
import com.microsoft.azure.management.servicebus.ServiceBusSubscription;
import com.microsoft.azure.management.servicebus.Topic;

public abstract class SubscriptionIterator extends TopicIterator {
	
	public void iterateThroughSubscriptionsForTopic(ServiceBusNamespace ns, Topic topic) throws Exception {
		handle(0,0, ns, topic);
	}
	
	@Override
	protected void handle(int nsIndex, int topicIndex, ServiceBusNamespace ns, Topic topic) throws Exception {
		iterateSubscriptionsInternal(nsIndex, topicIndex, ns, topic);
	}
	
	private void iterateSubscriptionsInternal(int nsIndex, int topicIndex, ServiceBusNamespace ns, Topic topic) throws Exception {
		int subIndex = 0;
		
		if(topic.subscriptionCount()>0) {
			for(ServiceBusSubscription sub: topic.subscriptions().list()) {
				handle(nsIndex, topicIndex, subIndex, ns, topic, sub);
				subIndex ++;
			}
		}
	}
	
	protected abstract void handle(int nsIndex, int topicIndex, int subIndex, ServiceBusNamespace ns, Topic topic, ServiceBusSubscription sub) throws Exception;

}
