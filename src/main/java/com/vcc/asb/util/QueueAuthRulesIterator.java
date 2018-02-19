package com.vcc.asb.util;

import com.microsoft.azure.management.servicebus.Queue;
import com.microsoft.azure.management.servicebus.QueueAuthorizationRule;
import com.microsoft.azure.management.servicebus.ServiceBusNamespace;

public abstract class QueueAuthRulesIterator extends QueueIterator {
	
	public void iterateThroughAuthRulesForQueue(ServiceBusNamespace ns, Queue queue) throws Exception {
		handle(0,0, ns, queue);
	}
	
	@Override
	protected void handle(int nsIndex, int queueIndex, ServiceBusNamespace ns, Queue queue) throws Exception {
		iteratorQueueAuthRulesInternal(nsIndex, queueIndex, ns, queue);
	}
	
	private void iteratorQueueAuthRulesInternal(int nsIndex, int queueIndex, ServiceBusNamespace ns, Queue queue) throws Exception {
		
		int authRulesIndex = 0;
		if(queue.authorizationRules()!=null && queue.authorizationRules().list()!=null && queue.authorizationRules().list().size()>0) {
			for(QueueAuthorizationRule queueAuthRule: queue.authorizationRules().list()) {
				
				handle(nsIndex, queueIndex, authRulesIndex, ns, queue, queueAuthRule);
			
			}
		}
		
	}
	
	protected abstract void handle(int nsIndex, int queueIndex, int authRulesIndex, ServiceBusNamespace ns, Queue queue, QueueAuthorizationRule queueAuthRule) throws Exception;

}
