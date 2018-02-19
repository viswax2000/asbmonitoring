package com.vcc.asb.util;

import com.microsoft.azure.management.servicebus.Queue;
import com.microsoft.azure.management.servicebus.ServiceBusNamespace;

public abstract class QueueIterator extends NamespaceIterator {
	
	public void iterateThroughQueuesForNamespace(ServiceBusNamespace ns) throws Exception {
		handle(0, ns);
	}
	
	@Override
	protected void handle(int nsIndex, ServiceBusNamespace ns) throws Exception {
		iterateQueuesInternal(nsIndex, ns);
	}
	
	private void iterateQueuesInternal(int nsIndex, ServiceBusNamespace ns) throws Exception {
		
		int queueIndex = 0;
		if(ns.queues()!=null && ns.queues().list()!=null && ns.queues().list().size()>0) {
			for(Queue queue: ns.queues().list()) {
				handle(nsIndex, queueIndex, ns, queue);
				queueIndex ++;
			}
		}
	}
	
	protected abstract void handle(int nsIndex, int queueIndex, ServiceBusNamespace ns, Queue queue) throws Exception;

}
