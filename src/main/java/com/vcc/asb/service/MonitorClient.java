package com.vcc.asb.service;

import java.net.URI;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.HeadersBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.vcc.asb.context.AppContext;

/**
 * REST-API Client to query the Azure Monitor API. Requires Bearer Token OAuth2 Access Token of a suitably
 * configured Service Principal on the Service Bus
 * 
 * @author Volvo-IT/HCL
 *
 */
@Service
public class MonitorClient extends BaseClient {
	
	private static Logger logger = LoggerFactory.getLogger(MonitorClient.class);
	
	//Add AAD Authorization by getting the Token
	public String getAADAccessToken() {
		
		String accessToken = null;
		
		logger.debug("Getting the AccessToken - start");
		
		try {
			accessToken = serviceConfig.getOAuth2AccessToken(AppContext.getDefaultTenantId(), AppContext.getClientId(), AppContext.getClientSecret());
			logger.debug("successfully got the oauth2 aad access token");
		} catch(Exception e) {
			logger.error("Caught Exception while getting Access Token",e);
			throw new RuntimeException(e);
		} 
		
		return accessToken;
	}
	
	/**
	 * Invokes the Azure Monitor REST API to get the Metrics for the Namespace.
	 * ClientRequest is parameterized based on the Response object, and contains the invokation details
	 */
	@Override
	public <T> boolean executeMetricsCommand(ClientRequest<T> request) {
		
		RestTemplate rt = null;
		Exception ex = null;
		boolean isSuccess = false;
		
		try {
			
			logger.debug("MonitorClient::Executing the Metrics Command, Entity:"+request.getEntityName()+", EntityType:"+request.getEntityType().name());
			
			rt = getRestTemplate();
			URI uri = new URI(request.getUri());
			
			HeadersBuilder headers = RequestEntity.get(uri);
			Properties props = request.getHeaders();
			
			for(Entry entry: props.entrySet()) {
				headers.header((String)entry.getKey(), (String)entry.getValue());
			}
			
			RequestEntity requestEntity = headers.build();
			
			ResponseEntity<T> responseEntity = null;
			
			if(request.getRequestMethod() == HttpMethod.GET) {
				responseEntity = rt.exchange(uri, request.getRequestMethod(), requestEntity, request.getResponseClass());
			}
			request.setResponse(responseEntity.getBody());
			
			if(responseEntity.getBody()!=null) {
				logger.debug(" MonitorClient::Executed MetricsCommand, Response Body Class :"+responseEntity.getBody().getClass().getSimpleName());
			}
			
			isSuccess = true;
		
		} catch(Exception e) {
			logger.error(" *** Caught Exception while executing the Metrics Command for Entity:"+request.getEntityName()+", EntityType:"+request.getEntityType().name());
			ex = e;
			isSuccess = false;
		} finally {
			if(rt!=null) {
				releaseRestTemplate(rt);
			}
		}
		
		if(ex!=null) {
			throw new RuntimeException(ex);
		}
		
		return isSuccess;
	}
	
	/**
	 * Invokes the Azure Monitor REST API to get the Configuration information for the messaging
	 * entities:Queue, Topic, Subscription. ClientRequest is parameterized based on the Response Entity, 
	 * and contains the invocation details 
	 * 
	 */
	@Override
	public <T> boolean executeConfigCommand(ClientRequest<T> request) {
		
		RestTemplate rt = null;
		boolean isSuccess = false;
		Exception ex = null;
		
		try {
			
			logger.debug("MonitorClient::Executing ConfigCommand, Entity:"+request.getEntityName()+",EntityType:"+request.getEntityType().name());

			rt = getRestTemplate();
			URI uri = new URI(request.getUri());
			
			HeadersBuilder headers = RequestEntity.get(uri);
			Properties props = request.getHeaders();
			
			for(Entry entry: props.entrySet()) {
				headers.header((String)entry.getKey(), (String)entry.getValue());
			}
			
			RequestEntity requestEntity = headers.build();
			ResponseEntity<T> responseEntity = null;
			
			if(request.getRequestMethod() == HttpMethod.GET) {
				responseEntity = rt.exchange(uri, request.getRequestMethod(), requestEntity, request.getResponseClass());
			}
			request.setResponse(responseEntity.getBody());

			if(responseEntity.getBody()!=null) {
				logger.debug("MonitorClient::Executed ConfigCommand, Response Class Name:"+responseEntity.getBody().getClass().getSimpleName());
			}
			
			isSuccess = true;
		
		} catch(Exception e) {
			ex = e;
			logger.error("Caught Exception while Executing ConfigCommand @ MonitoClient, Entity:"+request.getEntityName()+", EntityType:"+request.getEntityType().name());
			isSuccess = false;
		} finally {
			if(rt!=null) {
				releaseRestTemplate(rt);
			}
		}

		if(ex != null) {
			throw new RuntimeException(ex);
		}
		
		return isSuccess;
	}
	
	public void releaseRestTemplate(RestTemplate rt) {
		try {
			serviceConfig.releaseRestTemplateMonitorAPI(rt);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public RestTemplate getRestTemplate() {
		RestTemplate rt = null;
		try {
			rt = serviceConfig.getRestTemplateForMonitorAPI();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rt;
	}

}
