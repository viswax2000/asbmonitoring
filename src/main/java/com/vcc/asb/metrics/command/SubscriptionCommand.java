package com.vcc.asb.metrics.command;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import com.vcc.asb.config.model.SubscriptionDescription;
import com.vcc.asb.configuration.ConfigConstants;
import com.vcc.asb.configuration.EntityType;
import com.vcc.asb.configuration.MgmtApiConstants;
import com.vcc.asb.configuration.ServiceBusApiConstants;
import com.vcc.asb.context.AppContext;
import com.vcc.asb.context.BatchContext;
import com.vcc.asb.context.BatchType;
import com.vcc.asb.exception.ConfigException;
import com.vcc.asb.exception.MetricsException;
import com.vcc.asb.metrics.model.MetricValue;
import com.vcc.asb.metrics.model.MetricValueSet;
import com.vcc.asb.metrics.model.MetricValueSetCollection;
import com.vcc.asb.metrics.model.SubscriptionMetrics;
import com.vcc.asb.metrics.model.TimeGrain;
import com.vcc.asb.metrics.model.Timespan;
import com.vcc.asb.model.Subscription;
import com.vcc.asb.service.Client;
import com.vcc.asb.service.ClientRequest;
import com.vcc.asb.service.MgmtClient;
import com.vcc.asb.service.ServiceBusClient;

public class SubscriptionCommand<V> extends BaseCommand<V> {
	
	Subscription sub;
	V subCmdResult;
	
	private static Logger logger = LoggerFactory.getLogger(SubscriptionCommand.class);
	
	public SubscriptionCommand(Subscription sub) {
		this.sub = sub;
		if(BatchContext.getCurrentBatchType() == BatchType.METRICS) {
			this.subCmdResult = (V) (new SubscriptionMetrics());
		} else {
			this.subCmdResult = (V) (new SubscriptionDescription());
		}
	}
	
	public V getCommandResult() {
		return this.subCmdResult;
	}
	
	public EntityType getEntityType() {
		return EntityType.SUBSCRIPTION;
	}
	
	public Subscription getEntity() {
		return this.sub;
	}
	
	public List<MetricsType> getMetricsTypes() {
		return Arrays.asList(MetricsType.ROLLUP_METRICS, MetricsType.CONFIG_METRICS);
	}

	@Override
	public void execute(Client client) {
		
		try {
			
			if(BatchContext.getCurrentBatchType() == BatchType.METRICS) {
				if(getMetricsType() == MetricsType.ROLLUP_METRICS) {
					
					ClientRequest<MetricValueSetCollection> clRequest = getClientRequestForMgmtClientSubscriptionRollupMetrics(
																				getRequestUriForMgmtClientSubscriptionRollupMetrics());

					logger.debug("Executing SubscriptionCommand RollupMetrics @ MgmtClient, Subscription:"+this.sub.getSubscriptionName()+
											",Topic:"+this.sub.getTopicName()+",Namespace:"+this.sub.getNamespaceName());

					client.executeMetricsCommand(clRequest);
					
					if((clRequest.getResponse()!=null) && (clRequest.getResponse() instanceof MetricValueSetCollection)) {
						populateSubscriptionRollupMetrics(clRequest.getResponse());
					} else {
						commandSuccessful = false;
						throw new Exception("Unable to retrieve the Subscription Rollup Metrics @ MgmtClient for Subscription:"+this.sub.getSubscriptionName()+
								"Topic:"+this.sub.getTopicName()+",namespaceName:"+this.sub.getNamespaceName()+", ResourceGroupName:"+this.sub.getResourceGroupName());							
					}
					
				} else if(getMetricsType() == MetricsType.CONFIG_METRICS) {
					
					ClientRequest<String> clRequest = getClientRequestForSBClientSubscriptionConfigMetrics(getRequestUriForSBClientSubscriptionConfigMetrics());

					logger.debug("Executing SubscriptionCommand ConfigMetrics @ SBClient, Subscription:"+this.sub.getSubscriptionName()+
												",Topic:"+this.sub.getTopicName()+",Namespace:"+this.sub.getNamespaceName());
					
					client.executeMetricsCommand(clRequest);
					
					if(clRequest.getResponse()!=null) {
						populateSubscriptionConfigMetrics(clRequest.getResponse());
					} else {
						commandSuccessful = false;
						throw new Exception("Unable to retrieve the Subscription Config Metrics @ ServiceBusClient for Subscription:"+this.sub.getSubscriptionName()+
								"Topic:"+this.sub.getTopicName()+",namespaceName:"+this.sub.getNamespaceName()+", ResourceGroupName:"+this.sub.getResourceGroupName());							
					}
				}
				
			} else {
				
				if(client instanceof ServiceBusClient) {
					
					ClientRequest<String> clRequest = getClientRequestForSBClientSubscriptionConfig(getRequestUriForSBClientSubscriptionConfig());
					
					logger.debug("Executing SubscriptionCommand, Config @ SBClient, Subscription:"+this.sub.getSubscriptionName()+
							",Topic:"+this.sub.getTopicName()+",Namespace:"+this.sub.getNamespaceName());

					client.executeConfigCommand(clRequest);
					
					if(clRequest.getResponse()!=null) {
						this.subCmdResult = (V) AppContext.getJaxbUtils().getObjectFromXml(clRequest.getResponse(), SubscriptionDescription.class);
					} else {
						commandSuccessful = false;
						throw new Exception("Unable to retrieve the Subscription Config @ ServiceBusClient for Subscription:"+this.sub.getSubscriptionName()+
								"Topic:"+this.sub.getTopicName()+",namespaceName:"+this.sub.getNamespaceName()+", ResourceGroupName:"+this.sub.getResourceGroupName());							
						
					}
					
				} else if(client instanceof MgmtClient) {
					
					ClientRequest<String> clRequest = getClientRequestForMgmtClientSubscriptionConfig(getRequestUriForMgmtClientSubscriptionConfig());
					
					logger.debug("Executing SubscriptionCommand Config @ MgmtClient, Subscription:"+this.sub.getSubscriptionName()+
							",Topic:"+this.sub.getTopicName()+",Namespace:"+this.sub.getNamespaceName());

					client.executeConfigCommand(clRequest);
					
					if(clRequest.getResponse()!=null) {
						this.subCmdResult = (V) AppContext.getJaxbUtils().getObjectFromXml(clRequest.getResponse(), SubscriptionDescription.class);
					} else {
						commandSuccessful = false;
						throw new Exception("Unable to retrieve the Subscription Config @ MgmtClient for Subscription:"+this.sub.getSubscriptionName()+
								"Topic:"+this.sub.getTopicName()+",namespaceName:"+this.sub.getNamespaceName()+", ResourceGroupName:"+this.sub.getResourceGroupName());							
						
					}
				}
				
				logger.debug("Populating Default Values for Boolean Nulls");

				populateDefaultValuesForBooleans();
			}
			
			this.commandSuccessful = true;
		
		} catch(Exception e) {
			this.commandException = e;
			this.commandSuccessful = false;
			
			if(BatchContext.getCurrentBatchType() == BatchType.METRICS) {
				logger.error("Caught Exeption While Executing SubscriptionCommand for Metrics", e);
				throw new MetricsException("Caught Exeption While Executing SubscriptionCommand for Metrics", e);
			} else {
				logger.error("Caught Exeption While Executing SubscriptionCommand for Config", e);
				throw new ConfigException("Caught Exeption While Executing SubscriptionCommand for Config", e);
			}
		}
		
	}
	
	private void populateDefaultValuesForBooleans() {
		SubscriptionDescription sd = (SubscriptionDescription)this.subCmdResult;
		if(sd.isDeadLetteringOnFilterEvaluationExceptions()==null) {
			sd.setDeadLetteringOnFilterEvaluationExceptions(Boolean.FALSE);
		}
		if(sd.isDeadLetteringOnMessageExpiration()==null) {
			sd.setDeadLetteringOnMessageExpiration(Boolean.FALSE);
		}
		if(sd.isEnableBatchedOperations()==null) {
			sd.setEnableBatchedOperations(Boolean.FALSE);
		}
		if(sd.isRequiresSession()==null) {
			sd.setRequiresSession(Boolean.FALSE);
		}
	}
	
	private void populateSubscriptionConfigMetrics(String xmlResponse) throws Exception {
		
		logger.debug("Populating Subscription ConfigMetrics, Subscription:"+this.sub.getSubscriptionName()+",Topic:"+this.sub.getTopicName()+
																		",Namespace:"+this.sub.getNamespaceName());
		
		SubscriptionMetrics subMetrics = (SubscriptionMetrics) this.subCmdResult;
		
		SubscriptionDescription response = AppContext.getJaxbUtils().getObjectFromXml(xmlResponse, SubscriptionDescription.class);
		
		subMetrics.setLength(response.getMessageCount().intValue());
		subMetrics.setActiveMessageCount(response.getMessageCount().intValue());
		subMetrics.setDeadLetterMessageCount(response.getCountDetails().getDeadLetterMessageCount().intValue());
		subMetrics.setNamespaceName(this.sub.getNamespaceName());
		subMetrics.setResourceGroupName(this.sub.getResourceGroupName());
		subMetrics.setScheduledMessageCount(response.getCountDetails().getScheduledMessageCount().intValue());
		subMetrics.setTransferDeadLetterMessageCount(response.getCountDetails().getTransferDeadLetterMessageCount().intValue());
		subMetrics.setTransferMessageCount(response.getCountDetails().getTransferMessageCount().intValue());
		
		logger.debug("Populated Subscription ConfigMetrics Successfully");
		
	}

	private void populateSubscriptionRollupMetrics(MetricValueSetCollection response) {
		
		logger.debug("Populating Subscription RollupMetrics, Subscription:"+this.sub.getSubscriptionName()+",Topic:"+this.sub.getTopicName()+
											",Namespace:"+this.sub.getNamespaceName());

		SubscriptionMetrics subMetrics = (SubscriptionMetrics) this.subCmdResult;
		
		if(response!=null && response.getValue()!=null && response.getValue().getMetricValueSets()!=null && 
				response.getValue().getMetricValueSets().size()>0) {
			for(MetricValueSet metricValueSet: response.getValue().getMetricValueSets()) {
				String metricName = metricValueSet.getName();
				int total = 0;
				if(metricValueSet.getMetricValues()!=null && metricValueSet.getMetricValues().getMetricValues()!=null && 
						metricValueSet.getMetricValues().getMetricValues().size()>0) {
					for(MetricValue metricValue: metricValueSet.getMetricValues().getMetricValues()) {
						total = total + metricValue.getTotal();
					}
				}
				
				setMetricsProperty(subMetrics, metricName, total);
			}
		}
		
		Timespan ts = new Timespan();
		ts.setStartTime(this.startTime);
		ts.setEndTime(this.endTime);
		
		subMetrics.setTimespan(ts);
		subMetrics.setTimeInterval(TimeGrain.PT_5_M.value());
		subMetrics.setNamespaceName(this.sub.getNamespaceName());
		subMetrics.setResourceGroupName(this.sub.getResourceGroupName());
		subMetrics.setSubscriptionName(this.sub.getSubscriptionName());
		subMetrics.setTopicName(this.sub.getTopicName());
		
		logger.debug("Populated Subscription RollupMetrics Successfully");

	}
	
	private void setMetricsProperty(SubscriptionMetrics subMetrics, String metricName, int total) {
		if(metricName.equalsIgnoreCase("length")) {
			subMetrics.setLength(total);
		} else if(metricName.equalsIgnoreCase("outgoing")) {
			subMetrics.setOutgoing(total);
		} else if(metricName.equalsIgnoreCase("requests.successful")) {
			subMetrics.setRequestsSuccessful(total);
		} else if(metricName.equalsIgnoreCase("requests.total")) {
			subMetrics.setRequestsTotal(total);
		} else if(metricName.equalsIgnoreCase("requests.failed")) {
			subMetrics.setRequestsFailed(total);
		} else if(metricName.equalsIgnoreCase("requests.failed.serverbusy")) {
			subMetrics.setRequestsFailedServerbusy(total);
		} else if(metricName.equalsIgnoreCase("requests.failed.other")) {
			subMetrics.setRequestsFailedOther(total);
		} else if(metricName.equalsIgnoreCase("requests.failed.internalservererror")) {
			subMetrics.setRequestsFailedInternalservererror(total);
		}

	}

	private ClientRequest<String> getClientRequestForMgmtClientSubscriptionConfig(String requestUri) {
		
		ClientRequest<String> clRequest = new ClientRequest<String>();
		
		clRequest.setUri(requestUri);
		clRequest.setRequestMethod(HttpMethod.GET);
		clRequest.setBody(null);
		
		for(Entry<String,String> entry: MgmtApiConstants.SUBSCRIPTION_CONFIG_HEADERS.entrySet()) {
			clRequest.getHeaders().setProperty(entry.getKey(), entry.getValue());
		}
		
		clRequest.setEntityName(this.sub.getSubscriptionName());
		clRequest.setEntityType(EntityType.SUBSCRIPTION);
		clRequest.setNamespaceName(this.sub.getNamespaceName());
		clRequest.setParentTopicName(this.sub.getTopicName());
		clRequest.setResourceGroupName(this.sub.getResourceGroupName());
		clRequest.setResponseClass(String.class);

		return clRequest;
	}

	private String getRequestUriForMgmtClientSubscriptionConfig() {
		
		String uri = MgmtApiConstants.SUBSCRIPTION_CONFIG_METRICS_URL;
		
		uri = uri.replace(ConfigConstants.REPLACE_TENANT_ID, AppContext.getDefaultTenantId());
		uri = uri.replace(ConfigConstants.REPLACE_NAMESPACE_NAME, this.sub.getNamespaceName());
		uri = uri.replace(ConfigConstants.REPLACE_TOPIC_NAME, this.sub.getTopicName());
		uri = uri.replace(ConfigConstants.REPLACE_SUBSCRIPTION_NAME, this.sub.getSubscriptionName());
		
		return uri;
	}

	private ClientRequest<String> getClientRequestForSBClientSubscriptionConfig(String requestUri) {

		ClientRequest<String> clRequest = new ClientRequest<String>();
		
		clRequest.setUri(requestUri);
		clRequest.setBody(null);
		clRequest.setRequestMethod(HttpMethod.GET);
		clRequest.setResponseClass(String.class);
		
		for(Entry<String,String> entry: ServiceBusApiConstants.SUBSCRIPTION_CONFIG_HEADERS.entrySet()) {
			clRequest.getHeaders().setProperty(entry.getKey(), entry.getValue());
		}
		clRequest.getHeaders().setProperty(ConfigConstants.AUTHORIZATION_HEADER_NAME, this.sub.getSasTokenNamespaceRootKey());
		
		clRequest.setEntityName(this.sub.getSubscriptionName());
		clRequest.setEntityType(EntityType.SUBSCRIPTION);
		clRequest.setNamespaceName(this.sub.getNamespaceName());
		clRequest.setParentTopicName(this.sub.getTopicName());
		clRequest.setResourceGroupName(this.sub.getResourceGroupName());		
		
		return clRequest;
	}

	private String getRequestUriForSBClientSubscriptionConfig() {
		//"https://{namespaceName}.servicebus.windows.net/{topicName}/subscriptions/{subscriptionName}?api-version={api-version}"
		String uri = ServiceBusApiConstants.SUBSCRIPTION_CONFIG_URI;
		uri = uri.replace(ConfigConstants.REPLACE_NAMESPACE_NAME, this.sub.getNamespaceName());
		uri = uri.replace(ConfigConstants.REPLACE_TOPIC_NAME, this.sub.getTopicName());
		uri = uri.replace(ConfigConstants.REPLACE_SUBSCRIPTION_NAME, this.sub.getSubscriptionName());
		uri = uri.replace(ConfigConstants.REPLACE_API_VERSION, ServiceBusApiConstants.SUBSCRIPTION_CONFIG_API_VERSION);
		
		return uri;
	}

	public ClientRequest<MetricValueSetCollection> getClientRequestForMgmtClientSubscriptionRollupMetrics(String uri) {
		
		ClientRequest<MetricValueSetCollection> clRequest = new ClientRequest<MetricValueSetCollection>();
		clRequest.setUri(uri);
		clRequest.setBody(null);
		for(Entry<String,String> entry: MgmtApiConstants.SUBSCRIPTION_ROLLUP_METRICS_HEADERS.entrySet()) {
			clRequest.getHeaders().setProperty(entry.getKey(), entry.getValue());
		}
		clRequest.setRequestMethod(HttpMethod.GET);
		clRequest.setResponseClass(MetricValueSetCollection.class);
		
		clRequest.setEntityName(this.sub.getSubscriptionName());
		clRequest.setEntityType(EntityType.SUBSCRIPTION);
		clRequest.setNamespaceName(this.sub.getNamespaceName());
		clRequest.setParentTopicName(this.sub.getTopicName());
		clRequest.setResourceGroupName(this.sub.getResourceGroupName());		

		return clRequest;		
	}
	
	public String getRequestUriForMgmtClientSubscriptionRollupMetrics() {
		
		String uri = MgmtApiConstants.SUBSCRIPTION_ROLLUP_METRICS_URL;
		
		uri = uri.replace(ConfigConstants.REPLACE_SUBSCRIPTION_ID, AppContext.getDefaultSubscriptionId());
		uri = uri.replace(ConfigConstants.REPLACE_NAMESPACE_NAME, sub.getNamespaceName());
		uri = uri.replace(ConfigConstants.REPLACE_TOPIC_NAME, sub.getTopicName());
		uri = uri.replace(ConfigConstants.REPLACE_SUBSCRIPTION_NAME, sub.getSubscriptionName());
		uri = uri.replace(ConfigConstants.REPLACE_METRIC_NAMES, MgmtApiConstants.SUBSCRIPTION_ROLLUP_METRICS_NAMES);
		uri = uri.replace(ConfigConstants.REPLACE_TIME_GRAIN, TimeGrain.PT_5_M.value());
		
		if(BatchContext.getLastRun()==null) {
			startTime = DateTime.now().minusMinutes(5);
			endTime = DateTime.now();
		
		} else {
			startTime = BatchContext.getLastRun();
			endTime = DateTime.now();
		}
		
		String stTime = startTime.toString(AppContext.getMonitorMetricsDateTimeFormat());
		String enTime = endTime.toString(AppContext.getMonitorMetricsDateTimeFormat());
		uri = uri.replace(ConfigConstants.REPLACE_START_TIME, stTime);
		uri = uri.replace(ConfigConstants.REPLACE_END_TIME, enTime);
		
		return uri;
	}
	
	public ClientRequest<String> getClientRequestForSBClientSubscriptionConfigMetrics(String uri) {
		
		ClientRequest<String> clRequest = new ClientRequest<String>();
		clRequest.setUri(uri);
		clRequest.setBody(null);
		clRequest.setRequestMethod(HttpMethod.GET);
		
		for(Entry<String,String> entry: ServiceBusApiConstants.SUBSCRIPTION_CONFIG_HEADERS.entrySet()) {
			clRequest.getHeaders().setProperty(entry.getKey(), entry.getValue());
		}

		String sasToken = this.sub.getSasTokenNamespaceRootKey();
		clRequest.getHeaders().setProperty(ConfigConstants.AUTHORIZATION_HEADER_NAME, sasToken);
		
		clRequest.setEntityName(this.sub.getSubscriptionName());
		clRequest.setEntityType(EntityType.SUBSCRIPTION);
		clRequest.setNamespaceName(this.sub.getNamespaceName());
		clRequest.setParentTopicName(this.sub.getTopicName());
		clRequest.setResourceGroupName(this.sub.getResourceGroupName());		
		clRequest.setResponseClass(String.class);
		
		return clRequest;
	}
	
	public String getRequestUriForSBClientSubscriptionConfigMetrics() {

		String uri = ServiceBusApiConstants.SUBSCRIPTION_CONFIG_URI;
		uri = uri.replace(ConfigConstants.REPLACE_NAMESPACE_NAME, sub.getNamespaceName());
		uri = uri.replace(ConfigConstants.REPLACE_TOPIC_NAME, sub.getTopicName());
		uri = uri.replace(ConfigConstants.REPLACE_SUBSCRIPTION_NAME, sub.getSubscriptionName());
		uri = uri.replace(ConfigConstants.REPLACE_API_VERSION, ServiceBusApiConstants.SUBSCRIPTION_CONFIG_API_VERSION);
		
		return uri;
	}

}
