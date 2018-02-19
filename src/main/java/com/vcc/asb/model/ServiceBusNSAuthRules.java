package com.vcc.asb.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vcc.asb.config.model.AuthorizationRule;

public class ServiceBusNSAuthRules {
	
	private String namespaceName;
	private String resourceGroupName;
	private String subscriptionId;
	private String namespaceRootKeySASToken;
	
	private List<AuthorizationRule> nsAuthRules;
	private Map<String, List<AuthorizationRule>> queueAuthRules;
	private Map<String, List<AuthorizationRule>> topicAuthRules;
	
	public ServiceBusNSAuthRules() {
		nsAuthRules = new ArrayList<AuthorizationRule>();
		queueAuthRules = new HashMap<String, List<AuthorizationRule>>();
		topicAuthRules = new HashMap<String, List<AuthorizationRule>>();
	}

	public List<AuthorizationRule> getNsAuthRules() {
		return nsAuthRules;
	}

	public Map<String, List<AuthorizationRule>> getQueueAuthRules() {
		return queueAuthRules;
	}

	public Map<String, List<AuthorizationRule>> getTopicAuthRules() {
		return topicAuthRules;
	}

	public String getNamespaceName() {
		return namespaceName;
	}

	public void setNamespaceName(String namespaceName) {
		this.namespaceName = namespaceName;
	}

	public String getResourceGroupName() {
		return resourceGroupName;
	}

	public void setResourceGroupName(String resourceGroupName) {
		this.resourceGroupName = resourceGroupName;
	}

	public void setNamespaceRootKeySASToken(String sasToken) {
		this.namespaceRootKeySASToken = sasToken;
		
	}
	
	public String getNamespaceRootKeySASToken() {
		return this.namespaceRootKeySASToken;
	}
	
}
