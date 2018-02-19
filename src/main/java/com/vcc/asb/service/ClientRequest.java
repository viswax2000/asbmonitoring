package com.vcc.asb.service;

import java.util.Properties;

import org.springframework.http.HttpMethod;

import com.vcc.asb.configuration.EntityType;

public class ClientRequest<T> {
	
	private String uri;
	private String body;
	private Properties headers;
	private HttpMethod requestMethod;
	
	String entityName;
	EntityType entityType;
	String namespaceName;
	String resourceGroupName;
	String parentTopicName;
	
	T response;
	Class<T> responseClass;
	
	public ClientRequest() {
		headers = new Properties();
		requestMethod = HttpMethod.GET;
	}
	
	public Properties getHeaders() {
		return this.headers;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public HttpMethod getRequestMethod() {
		return requestMethod;
	}
	
	public void setRequestMethod(HttpMethod method) {
		this.requestMethod = method;
	}
	
	public void setResponse(T response) {
		this.response = response; 
	}
	
	public T getResponse() {
		return this.response;
	}
	
	public void setResponseClass(Class<T> clazz) {
		this.responseClass = clazz;
	}
	
	public Class<T> getResponseClass() {
		return this.responseClass;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
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

	public String getParentTopicName() {
		return parentTopicName;
	}

	public void setParentTopicName(String parentTopicName) {
		this.parentTopicName = parentTopicName;
	}
	
	
	

}
