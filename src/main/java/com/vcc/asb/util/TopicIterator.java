package com.vcc.asb.util;

import com.microsoft.azure.management.servicebus.ServiceBusNamespace;
import com.microsoft.azure.management.servicebus.Topic;


public abstract class TopicIterator extends NamespaceIterator {
	
	public void iterateThroughTopicsForNamespace(ServiceBusNamespace ns) throws Exception {
		handle(0, ns);
	}
	
	@Override
	protected void handle(int nsIndex, ServiceBusNamespace ns) throws Exception {
		iterateTopicsInternal(nsIndex, ns);
	}
	
	private void iterateTopicsInternal(int nsIndex, ServiceBusNamespace ns) throws Exception {
		
		int topicIndex = 0;
		if(ns.topics()!=null && ns.topics().list()!=null && ns.topics().list().size()>0) {
			for(Topic topic: ns.topics().list()) {
				handle(nsIndex, topicIndex, ns, topic);
				topicIndex ++;
			}
		}
	}
	
	protected abstract void handle(int nsIndex, int topicIndex, ServiceBusNamespace ns, Topic topic) throws Exception;

}
