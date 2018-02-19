package com.vcc.asb.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpMethod;

/**
 * URLs, Header and Request Parameters for the Management API calls for Queue,Topic,Subscription. 
 * Can retrieve both the Configuration and Rollup-Metrics Information for these Entities.  
 * 
 * @author HCL/VOLVO-IT
 *
 */
public class MgmtApiConstants {
	
	//************************************************* CONFIGURATION METRICS ***********************************************************//
	//https://management.core.windows.net/adb1c381-b337-4db1-889b-e8dae5fbb822/services/ServiceBus/Namespaces/vccrgns3dc2178016/Queues/queue1_7274330208fa
	public static final String QUEUE_CONFIG_METRICS_URL = "https://management.core.windows.net/{tenantId}/services/ServiceBus/Namespaces/{namespaceName}/Queues/{queueName}";
	public static final HashMap<String,String> QUEUE_CONFIG_METRICS_HEADERS = new HashMap<String,String>() {
		{
			put("x-ms-version","2015-08-01");
		}
	};
	public static final HttpMethod QUEUE_CONFIG_METRICS_REQUEST_METHOD = HttpMethod.GET;
	
	public static final String TOPIC_CONFIG_METRICS_URL = "https://management.core.windows.net/{tenantId}/services/ServiceBus/Namespaces/{namespaceName}/Topics/{topicName}";
	public static final HashMap<String,String> TOPIC_CONFIG_METRICS_HEADERS = new HashMap<String,String>() {
		{
			put("x-ms-version","2015-08-01");
		}
	};
	public static final HttpMethod TOPIC_CONFIG_METRICS_REQUEST_METHOD = HttpMethod.GET;
	
	public static final String SUBSCRIPTION_CONFIG_METRICS_URL = "https://management.core.windows.net/{tenantId}/services/ServiceBus/Namespaces/{namespaceName}/Topics/"
															+ "{topicName}/Subscriptions/{subscriptionName}";
	public static final HashMap<String,String> SUBSCRIPTION_CONFIG_METRICS_HEADERS = new HashMap<String,String>() {
		{
			put("x-ms-version","2015-08-01");
		}
	};
	public static final HttpMethod SUBSCRIPTION_CONFIG_METRICS_REQUEST_METHOD = HttpMethod.GET;
	
	//********************************************************* ROLLUP METRICS *****************************************************************//
	//METRICS URLs: startTime, endTime in format 2018-01-07T15:00:00Z, timeGrain in formats PT5M, PT1H, P1D, P7D 
	public static final String QUEUE_ROLLUP_METRICS_URL = "https://management.core.windows.net/{subscriptionId}/services/monitoring/metricvalues/query?" +
												   "resourceId=/ServiceBus/Namespaces/{namespaceName}/Queues/{queueName}&names={metricsNames}" +
												   "&timeGrain={timeGrain}&startTime={startTime}&endTime={endTime}";
	
	public static final String QUEUE_ROLLUP_METRICS_NAMES = "length,size,incoming,outgoing,requests.total,requests.successful,requests.failed,"
															+ "requests.failed.internalservererror,requests.failed.serverbusy,requests.failed.other";
	
	public static final HashMap<String,String> QUEUE_ROLLUP_METRICS_HEADERS = new HashMap<String,String>() { {
			put("x-ms-version","2013-11-01");
			put("Accept", "application/xml");
	}};
	
	public static final HttpMethod QUEUE_ROLLUP_METRICS_REQUEST_METHOD = HttpMethod.GET;
	
	
	public static final String TOPIC_ROLLUP_METRICS_URL = "https://management.core.windows.net/{subscriptionId}/services/monitoring/metricvalues/query?"
												 + "resourceId=/ServiceBus/Namespaces/{namespaceName}/Topics/{topicName}"
												 + "&names={metricsNames}&timeGrain={timeGrain}&startTime={startTime}&endTime={endTime}";
	
	public static final String TOPIC_ROLLUP_METRICS_NAMES = "size,incoming,requests.total,requests.successful,requests.failed,requests.failed.internalservererror,"
			+ "requests.failed.serverbusy,requests.failed.other";
	
	public static final HashMap<String,String> TOPIC_ROLLUP_METRICS_HEADERS = new HashMap<String,String>() { {
			put("x-ms-version","2013-11-01");
			put("Accept", "application/xml");
	 }};
	
	public static final HttpMethod TOPIC_ROLLUP_METRICS_REQUEST_METHOD = HttpMethod.GET;
	
	
	public static final String SUBSCRIPTION_ROLLUP_METRICS_URL = "https://management.core.windows.net/{subscriptionId}/services/monitoring/metricvalues/query?"
												 + "resourceId=/ServiceBus/Namespaces/{namespaceName}/Topics/{topicName}/Subscriptions/{subscriptionName}"
												 + "&names={metricsNames}&timeGrain={timeGrain}&startTime={startTime}&endTime={endTime}";
	
	public static final HashMap<String,String> SUBSCRIPTION_ROLLUP_METRICS_HEADERS = new HashMap<String,String>() {{
			put("x-ms-version","2013-11-01");
			put("Accept", "application/xml");
	 }};
	
	public static final String SUBSCRIPTION_ROLLUP_METRICS_NAMES = "length,outgoing,requests.total,requests.successful,requests.failed,"
																   + "requests.failed.internalservererror,requests.failed.serverbusy,requests.failed.other";
	
	public static final HttpMethod SUBSCRIPTION_ROLLUP_METRICS_REQUEST_METHOD = HttpMethod.GET;
	
	/**************************  CONFIGURATION CONSTANTS *************************/
	
	public static final String QUEUE_CONFIG_URL = "https://management.core.windows.net/{tenantId}/services/ServiceBus/Namespaces/{namespaceName}/queues/{queueName}";
	public static final HttpMethod QUEUE_CONFIG_REQUEST_METHOD = HttpMethod.GET;
	public static final HashMap<String,String> QUEUE_CONFIG_HEADERS = new HashMap<String,String>() {
		{
			put("x-ms-version", "2013-11-01");
			put("Accept","application/xml");
		}
	};
	
	public static final String TOPIC_CONFIG_URL = "https://management.core.windows.net/{tenantId}/services/ServiceBus/Namespaces/{namespaceName}/topics/{topicName}";
	public static final HttpMethod TOPIC_CONFIG_REQUEST_METHOD = HttpMethod.GET;
	public static final HashMap<String,String> TOPIC_CONFIG_HEADERS = new HashMap<String,String>() {
		{
			put("x-ms-version", "2013-11-01");
			put("Accept","application/xml");
		}
	};
	
	public static final String SUBSCRIPTION_CONFIG_URL = "https://management.core.windows.net/{tenantId}/services/ServiceBus/Namespaces/{namespaceName}/topics/{topicName}/subscriptions/{subscriptionName}";
	public static final HttpMethod SUBSCRIPTION_CONFIG_REQUEST_METHOD = HttpMethod.GET;
	public static final HashMap<String,String> SUBSCRIPTION_CONFIG_HEADERS = new HashMap<String,String>() {
		{
			put("x-ms-version", "2013-11-01");
			put("Accept","application/xml");
		}
	};
	
	
	//**** Do not use the below to get NamespaceDescription, it doesnt fetch the full Namespace description .. 
	public static final String NAMESPACE_CONFIG_URL = "https://management.core.windows.net/{tenantId}/services/ServiceBus/Namespaces/{namespaceName}";
	public static final HashMap<String,String> NAMESPACE_CONFIG_HEADERS = new HashMap<String,String>() {
		{
			put("x-ms-version","2015-08-01");
			put("Accept","application/xml");
		}
	};
	public static final HttpMethod NAMESPACE_CONFIG_REQUEST_METHOD = HttpMethod.GET;
	
	
}
