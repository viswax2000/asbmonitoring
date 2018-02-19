package com.vcc.asb.util;

import com.microsoft.azure.management.servicebus.ServiceBusNamespace;
import com.microsoft.azure.management.servicebus.ServiceBusNamespaces;

public abstract class NamespaceIterator {
	
	public void iterateThroughNamespaces(ServiceBusNamespaces sbNamespaces) throws Exception {
		
		int nsIndex = 0;
		
		for(ServiceBusNamespace sbNS: sbNamespaces.list()) {
			handle(nsIndex, sbNS);
			nsIndex ++;
			
		}
		
	}
	
	protected abstract void handle(int namespaceIndex, ServiceBusNamespace ns) throws Exception;

}
