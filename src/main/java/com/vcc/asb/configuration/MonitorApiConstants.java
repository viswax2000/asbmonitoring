package com.vcc.asb.configuration;

import java.util.HashMap;

import org.springframework.http.HttpMethod;

/**
 * Used to retrieve ONLY the Namespace Level Metrics. Of the total 10 Metric Names for the Namespace,
 * each request can accomodate only 5 metric names. This can also retrieve Configuration info
 * for the Entities but try the Management API before that as it gives full config info. This uses the 
 * AAD Bearer Token based on a suitably configured AAD Application.
 * 
 * @author HCL/Volvo-IT
 *
 */
public class MonitorApiConstants {
	
	//Azure Monitor API can retrieve only Namespace level metrics
	//https://management.azure.com/subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/Microsoft.ServiceBus/namespaces/{namespaceName}/
	//Queues/{queueName}?api-version=2015-08-01
	//Azure Monitor API can also retrieve Queue, Topic,Subscription definition
	//*** Do not Use this to retrieve the Config as only fewer details are retrieved
	public static final String QUEUE_CONFIG_URL = "https://management.azure.com/subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/"
												+ "Microsoft.ServiceBus/namespaces/{namespaceName}/queues/{queueName}?api-version={api-version}";
	public static final String QUEUE_CONFIG_API_VERSION = "2015-08-01";
	
	public static final String NAMESPACE_CONFIG_URL = "https://management.azure.com/subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/"
													+ "Microsoft.ServiceBus/namespaces/{namespaceName}?api-version={api-version}";
	public static final String NAMESPACE_CONFIG_API_VERSION = "2015-08-01";
	
	public static final String TOPIC_CONFIG_URL = "https://management.azure.com/subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/"
												+ "Microsoft.ServiceBus/namespaces/{namespaceName}/topics/{topicName}?api-version={api-version}";
	public static final String TOPIC_CONFIG_API_VERSION = "2015-08-01";
	
	public static final String SUBSCRIPTION_CONFIG_URL = "https://management.azure.com/subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/"
													   + "Microsoft.ServiceBus/namespaces/{namespaceName}/topics/{topicName}/Subscriptions/{subscriptionName}"
													   + "?api-version={api-version}";
	public static final String SUBSCRIPTION_CONFIG_API_VERSION = "2015-08-01";
	
	
	public static final String NAMESPACE_METRICS_NAMES_1 = "SuccessfulRequests,ServerErrors,UserErrors,ThrottledRequests,IncomingRequests";
	
	public static final String NAMESPACE_METRICS_NAMES_2 = "IncomingMessages,OutgoingMessages,ActiveConnections,ConnectionsOpened,ConnectionsClosed";
	
	public static final String NAMESPACE_METRICS_API_VERSION = "2017-05-01-preview";
	
	//timespan format = 2018-01-07T15:08:00Z/2018-01-07T15:09:00Z
	//2017-05-01-preview
	public static final String NAMESPACE_METRICS_URL = "https://management.azure.com/subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/"
													 + "providers/Microsoft.ServiceBus/namespaces/{namespaceName}/providers/microsoft.insights/metrics"
													 + "?api-version={api-version}&timespan={timespan}&metric={metricsNames}&aggregation=Total&interval={timegrain}";
	
	public static final HttpMethod NAMESPACE_METRICS_REQUEST_METHOD = HttpMethod.GET;
	

}
