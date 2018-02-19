package com.vcc.asb.util;

import com.microsoft.azure.management.servicebus.ServiceBusNamespace;
import com.microsoft.azure.management.servicebus.Topic;
import com.microsoft.azure.management.servicebus.TopicAuthorizationRule;

public abstract class TopicAuthRulesIterator extends TopicIterator {
	
	public void iterateThroughTopicAuthorizationRules(int nsIndex, int topicIndex, ServiceBusNamespace ns, Topic topic) throws Exception {
		handle(nsIndex, topicIndex, ns, topic);
	}
	
	@Override
	protected void handle(int nsIndex, int topicIndex, ServiceBusNamespace ns, Topic topic) throws Exception {
		iterateTopicAuthRulesInternal(nsIndex, topicIndex, ns, topic);
	}
	
	private void iterateTopicAuthRulesInternal(int nsIndex, int topicIndex, ServiceBusNamespace ns, Topic topic) throws Exception {
		if(topic.authorizationRules()!=null && topic.authorizationRules().list().size()>0) {
			int topicAuthRuleIndex = 0;
			for(TopicAuthorizationRule topicAuthRule: topic.authorizationRules().list()) {
				handle(nsIndex, topicIndex, topicAuthRuleIndex, ns, topic, topicAuthRule);
				topicAuthRuleIndex ++;
				
			}
		}
	}
	
	protected abstract void handle(int nsIndex, int topicIndex, int topicAuthRuleIndex, ServiceBusNamespace ns, Topic topic, TopicAuthorizationRule topicAuthRule) throws Exception;
	

}
