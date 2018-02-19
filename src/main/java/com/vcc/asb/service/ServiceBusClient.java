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

/**
 * REST Client to invoke the ServiceBus API Endpoints to get the Metrics, and Config information
 * for Queue,Topic,Subscriptions. Requires the SAS Access Token as Authorization during invokation
 * 
 * @author viswanath.n
 *
 */
@Service
public class ServiceBusClient extends BaseClient {

	private static Logger logger = LoggerFactory.getLogger(ServiceBusClient.class);
	
	/**
	 * Invoke the ServiceBus API to get the Config Metrics for Queue,Topic,Subscription. ClientRequest
	 * contains the details of the invokation, including the Authorization header containing the SAS Token
	 * 
	 */
	@Override
	public <T> boolean executeMetricsCommand(ClientRequest<T> request) {
		
		RestTemplate rt = null;
		Exception ex = null;
		boolean isSuccess = false;
		
		try {
			logger.debug("Executing MetricsCommand @ ServiceBusClient, Entity:"+request.getEntityName()+", EntityType:"+request.getEntityType().name());
			
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
				logger.debug(" Executed MetricsCommand @ SBClient, Response ClassName:"+responseEntity.getBody().getClass().getSimpleName());
			}
			
			isSuccess = true;
		
		} catch(Exception e) {
			ex = e;
			isSuccess = false;
			logger.error("Exception while executing MetricsCommand @ SBClient, Entity:"+request.getEntityName()+",EntityType:"+request.getEntityType().name());
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
	 * Invokes the ServiceBus API endpoints to get the Configuration information for Queue,Topic,Subscription
	 * ClientRequest contains the invokation details including the Authorization header containing the SAS Token
	 * 
	 */
	@Override
	public <T> boolean executeConfigCommand(ClientRequest<T> request) {
		
		RestTemplate rt = null;
		Exception ex = null;
		boolean isSuccess = false;
		
		try {
			logger.debug("Executing ConfigCommand @ SBClient, Entity:"+request.getEntityName()+", EntityType:"+request.getEntityType().name());
			
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
				logger.debug("Executed ConfigCommand @ SBClient, Response ClassName:"+responseEntity.getBody().getClass().getSimpleName());
			}
			
			isSuccess = true;
		
		} catch(Exception e) {
			ex = e;
			isSuccess = false;
			logger.error("Exception while executing ConfigCommand @ SBClient, Entity:"+request.getEntityName()+",EntityType:"+request.getEntityType().name());
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
	
	public void releaseRestTemplate(RestTemplate rt) {
		try {
			serviceConfig.releaseRestTemplateServiceBusAPI(rt);
		} catch(Exception e) {
			logger.error("Exception while releasing RestTemplate@ServiceBusAPI", e);
			throw new RuntimeException(e);
		}
		
	}
	
	public RestTemplate getRestTemplate() {
		RestTemplate rt = null;
		try {
			rt = serviceConfig.getRestTemplateForServiceBusAPI();
		} catch(Exception e) {
			logger.error("Exception while getting the RestTemplate@ServiceBusAPI", e);
			throw new RuntimeException(e);
		}
		return rt;
	}	

}
