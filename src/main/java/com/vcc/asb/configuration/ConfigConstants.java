package com.vcc.asb.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMethod;

public interface ConfigConstants {
	
	public static final Map<String,String> AAD_TOKEN_HEADERS = new HashMap<String,String>() {
		{
			put("Content-Type", "application/x-www-form-urlencoded");
			put("Accept", "application/json");
		}
	};
	public static final String AAD_TOKEN_ENDPOINT_1 = "https://login.windows.net/{tenantId}/oauth2/token";
	public static final String AAD_TOKEN_ENDPOINT_2 = "https://login.microsoftonline.com/{tenantId}/oauth2/token";
	public static final String AAD_TOKEN_BODY = "grant_type=client_credentials&client_id={clientId}&resource={resourceUrl}&client_secret={clientSecret}";
	public static final RequestMethod AAD_TOKEN_REQUEST_METHOD = RequestMethod.POST;
	public static final String AAD_RESOURCE_URI = "https://management.core.windows.net/"; 


	public static final String AZURE_MONITOR_METRICS_API_CLIENT_VERSION = "2017-05-01-preview";
	//2018-01-07T00:00:01Z/2018-01-07T00:01:01Z
	public static final String AZURE_MONITOR_METRICS_API_URL = "https://management.azure.com/subscriptions/{subId}/resourceGroups/{resGroupId}/providers/Microsoft.ServiceBus/" +
										 "namespaces/{NamespaceName}/providers/microsoft.insights/metrics?api-version={azureApiVersion}&timespan={timespan}&" +
										 "metric={metrics}&aggregation=count,average&interval={timegrain}";
	

	public static final String AMPERSAND = "&";
	public static final String ROOT_MANAGE_SHARED_ACCESS_KEY = "RootManageSharedAccessKey";
	public static final String SASTOKEN_NAMESPACE_RESOURCE_URI = "https://{namespaceName}.servicebus.windows.net/";
	public static final String SASTOKEN_QUEUE_RESOURCE_URI = "https://{namespaceName}.servicebus.windows.net/{queueName}";
	public static final String SASTOKEN_TOPIC_RESOURCE_URI = "https://{namespaceName}.servicebus.windows.net/{topicName}";
	public static final String SASTOKEN_SUBSCRIPTION_RESOURCE_URI = "https://{namespaceName}.servicebus.windows.net/{topicName}/subscriptions/{subscriptionName}";
	public static final String AUTHORIZATION_HEADER_NAME = "Authorization";
	
	public static final String REPLACE_API_VERSION = "{api-version}";
	public static final String REPLACE_QUEUE_NAME = "{queueName}";
	public static final String REPLACE_TENANT_ID = "{tenantId}";
	public static final String REPLACE_NAMESPACE_NAME = "{namespaceName}";
	public static final String REPLACE_TOPIC_NAME = "{topicName}";
	public static final String REPLACE_SUBSCRIPTION_NAME = "{subscriptionName}";
	public static final String REPLACE_SUBSCRIPTION_ID = "{subscriptionId}";
	public static final String REPLACE_METRIC_NAMES = "{metricsNames}";
	public static final String REPLACE_TOPIC_METRIC_NAMES = "{topicMetricsNames}";
	public static final String REPLACE_TIME_GRAIN = "{timeGrain}";
	public static final String REPLACE_START_TIME = "{startTime}";
	public static final String REPLACE_END_TIME = "{endTime}";
	public static final String BLANK = "";
	

}
