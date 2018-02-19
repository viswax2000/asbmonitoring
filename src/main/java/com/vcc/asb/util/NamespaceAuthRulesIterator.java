package com.vcc.asb.util;

import com.microsoft.azure.management.servicebus.NamespaceAuthorizationRule;
import com.microsoft.azure.management.servicebus.ServiceBusNamespace;

public abstract class NamespaceAuthRulesIterator extends NamespaceIterator {
	
	public void iterateThroughAuthRulesForNamespace(ServiceBusNamespace ns) throws Exception {
		handle(0, ns);
	}
	
	@Override
	protected void handle(int nsIndex, ServiceBusNamespace ns) throws Exception {
		iterateNamespaceAuthRulesInternal(nsIndex, ns);
	}
	
	private void iterateNamespaceAuthRulesInternal(int nsIndex, ServiceBusNamespace ns) throws Exception {
		
		int authRuleIndex = 0;
		
		if(ns.authorizationRules()!=null && ns.authorizationRules().list()!=null && 
				ns.authorizationRules().list().size()>0) {
			for(NamespaceAuthorizationRule nsAuthRule: ns.authorizationRules().list()) {
				handle(nsIndex, authRuleIndex, ns, nsAuthRule);
				authRuleIndex ++;
			}
		}
	}
	
	protected abstract void handle(int nsIndex, int authRuleIndex, ServiceBusNamespace ns, NamespaceAuthorizationRule nsAuthRule) throws Exception;
	

}
