package com.vcc.asb.configuration;

import java.util.HashMap;

import org.springframework.http.HttpMethod;

public class ServiceBusApiConstants {
	
	
	public static final String QUEUE_CONFIG_URI = "https://{namespaceName}.servicebus.windows.net/{queueName}?api-version={api-version}";
	public static final String QUEUE_CONFIG_API_VERSION = "2013-07";
	public static final HashMap<String,String> QUEUE_CONFIG_HEADERS = new HashMap<String,String>() {
		{
			put("Accept", "application/xml");
		}
	};
	public static final HttpMethod QUEUE_CONFIG_REQUEST_METHOD = HttpMethod.GET;
	
	public static final String TOPIC_CONFIG_URI = "https://{namespaceName}.servicebus.windows.net/{topicName}?api-version={api-version}";
	public static final String TOPIC_CONFIG_API_VERSION = "2013-07";
	public static final HashMap<String,String> TOPIC_CONFIG_HEADERS = new HashMap<String,String>() {
		{
			put("Accept", "application/xml");
		}
	};	
	public static final HttpMethod TOPIC_CONFIG_REQUEST_METHOD = HttpMethod.GET;

	public static final String SUBSCRIPTION_CONFIG_URI = "https://{namespaceName}.servicebus.windows.net/{topicName}/subscriptions/{subscriptionName}?api-version={api-version}";
	public static final String SUBSCRIPTION_CONFIG_API_VERSION = "2013-07";
	public static final HashMap<String,String> SUBSCRIPTION_CONFIG_HEADERS = new HashMap<String,String>() {
		{
			put("Accept", "application/xml");
		}
	};
	public static final HttpMethod SUBSCRIPTION_CONFIG_REQUEST_METHOD = HttpMethod.GET;

}
