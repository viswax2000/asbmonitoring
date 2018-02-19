package com.vcc.asb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.microsoft.azure.management.Azure;
import com.vcc.asb.configuration.ServiceConfig;

public abstract class BaseClient implements Client {
	
	@Autowired
	ServiceConfig serviceConfig;
	
	public <T> boolean executeMetricsCommand(ClientRequest<T> request) {
		return false;
	}
	
	public <T> boolean executeConfigCommand(ClientRequest<T> request) {
		return false;
	}
	
	public String getDefaultSubscriptionId() {
		return serviceConfig.getDefaultSubscriptionId();
	}
	
	public String getTenantId() {
		return serviceConfig.getTenantId();
	}
	
	public String getDefaultResourceGroupName() {
		return serviceConfig.getDefaultResourceGroupName();
	}
	
	public String getDefaultNamespaceName() {
		return serviceConfig.getDefaultNamespaceName();
	}
	
	public String getMetricsTimespan() {
		return serviceConfig.getDefaultMetricsTimespan();
	}
	
	@Override
	public String getAADAccessToken() {
		return "";
	}
	
	@Override
	public abstract RestTemplate getRestTemplate();
	
	@Override
	public abstract void releaseRestTemplate(RestTemplate rt);

	public Azure getAzureService() {
		return serviceConfig.getAzureService();
	}

}
