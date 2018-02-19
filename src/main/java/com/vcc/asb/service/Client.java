package com.vcc.asb.service;

import java.util.HashMap;

import org.springframework.web.client.RestTemplate;

import com.vcc.asb.metrics.command.BaseCommand;

public interface Client {
	
	public <T> boolean executeMetricsCommand(ClientRequest<T> request);
	public <T> boolean executeConfigCommand(ClientRequest<T> request);
	//public HashMap<String,String> getUriParams(BaseCommand command);
	public RestTemplate getRestTemplate();
	
	public String getDefaultSubscriptionId();
	public String getTenantId();
	public String getDefaultResourceGroupName();
	public String getMetricsTimespan();
	public String getDefaultNamespaceName();
	public String getAADAccessToken();
	public void releaseRestTemplate(RestTemplate rt);
}
