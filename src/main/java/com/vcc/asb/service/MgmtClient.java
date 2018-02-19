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
 * REST Client to invoke the Azure Management API (RESTAPI URI --> https://management.core.windows.net/...) 
 * endpoints to get the RollupMetrics and Configuration for Queue,Topic,Subscription.
 * Requires the Management Certificate during Invokation, hence the RestTemplate need to be
 * configured with SSLContext
 * 
 * @author Volvo-IT/HCL
 *
 */
@Service
public class MgmtClient extends BaseClient {
	
	private static Logger logger = LoggerFactory.getLogger(MgmtClient.class);
	
	/**
	 * Invoke the Azure Management API to get the Rollup metrics for Queue, Topic, Subscription. ClientRequest
	 * contains the invokation details
	 */
	@Override
	public <T> boolean executeMetricsCommand(ClientRequest<T> request) {
		
		RestTemplate rt = null;
		boolean isSuccess = false;
		Exception ex = null;
		try {
			
			logger.debug("Executing MetricsCommand @ MgmtClient, Entity:"+request.getEntityName()+", EntityType:"+request.getEntityType().name());
			
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
				logger.debug("Executed MetricsCommand @ MgmtClient, Response Type:"+responseEntity.getBody().getClass().getSimpleName());
			}			
			
			isSuccess = true;
		
		} catch(Exception e) {
			ex = e;
			isSuccess = false;
			logger.error("Exception caught while executing MetricsCommand @ MgmtClient", e);
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
	 * Invoke the Azure Management API endpoints to get the Configuration for Queue,Topic,Subscription
	 * ClientRequest parameter contains the invokation details
	 * 
	 */
	@Override
	public <T> boolean executeConfigCommand(ClientRequest<T> clRequest) {
		
		RestTemplate rt = null;
		boolean isSuccess = false;
		Exception ex = null;
		
		try {
			
			logger.debug("Executing ConfigCommand @ MgmtClient, Entity:"+clRequest.getEntityName()+", EntityType:"+clRequest.getEntityType().name());
			
			rt = getRestTemplate();
			URI uri = new URI(clRequest.getUri());
			
			HeadersBuilder headers = RequestEntity.get(uri);
			Properties props = clRequest.getHeaders();
			
			for(Entry entry: props.entrySet()) {
				headers.header((String)entry.getKey(), (String)entry.getValue());
			}
			
			RequestEntity requestEntity = headers.build();
			ResponseEntity<T> responseEntity = null;
			
			if(clRequest.getRequestMethod() == HttpMethod.GET) {
				responseEntity = rt.exchange(uri, clRequest.getRequestMethod(), requestEntity, clRequest.getResponseClass());
			}
			
			clRequest.setResponse(responseEntity.getBody());
			if(responseEntity.getBody()!=null) {
				logger.debug("Executed ConfigCommand @ MgmtClient, Response Type:"+responseEntity.getBody().getClass().getSimpleName());
			}
			
			isSuccess = true;			
			
			
		} catch(Exception e) {
			ex = e;
			isSuccess = false;
			logger.error("Exception caught while executing ConfigCommand @ MgmtClient", e);
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
	
	public RestTemplate getRestTemplate() {
		RestTemplate rt = null;
		try {
			rt = serviceConfig.getRestTemplateForMgmtAPI();
		} catch(Exception e) {
			logger.error("Got Exception while fetching RestTemplateForMgmtAPI", e);
			throw new RuntimeException(e);
		}
		return rt;
	}
	
	public void releaseRestTemplate(RestTemplate rt) {
		try {
			serviceConfig.releaseRestTemplateMgmtAPI(rt);
		} catch(Exception e) {
			logger.error("Got Exception while fetching RestTemplateForMgmtAPI", e);
			throw new RuntimeException(e);
		}
		
	}

}
