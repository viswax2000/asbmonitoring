package com.vcc.asb.metrics.command;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import com.vcc.asb.config.model.TopicDescription;
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
import com.vcc.asb.metrics.model.TimeGrain;
import com.vcc.asb.metrics.model.Timespan;
import com.vcc.asb.metrics.model.TopicMetrics;
import com.vcc.asb.model.Topic;
import com.vcc.asb.service.Client;
import com.vcc.asb.service.ClientRequest;
import com.vcc.asb.service.MgmtClient;
import com.vcc.asb.service.ServiceBusClient;

public class TopicCommand<V> extends BaseCommand<V> {

	private Topic topic;
	private V topicCmdResult;
	
	private static Logger logger = LoggerFactory.getLogger(TopicCommand.class);
	
	public TopicCommand(Topic topic) {
		this.topic = topic;
		if(BatchContext.getCurrentBatchType() == BatchType.METRICS) {
			this.topicCmdResult = (V) new TopicMetrics();
		} else {
			this.topicCmdResult = (V) new TopicDescription();
		}
	}
	
	public EntityType getEntityType() {
		return EntityType.TOPIC;
	}
	
	public Topic getEntity() {
		return this.topic;
	}
	
	public V getCommandResult() {
		return this.topicCmdResult;
	}
	
	public List<MetricsType> getMetricsTypes() {
		return Arrays.asList(MetricsType.ROLLUP_METRICS, MetricsType.CONFIG_METRICS);
	}

	@Override
	public void execute(Client client) {
		
		try {
		
			if(BatchContext.getCurrentBatchType() == BatchType.METRICS) {
				if(getMetricsType() == MetricsType.ROLLUP_METRICS) {
					
					ClientRequest<MetricValueSetCollection> clRequest = getClientRequestForMgmtClientTopicRollupMetrics(getRequestUriForMgmtClientTopicRollupMetrics());
					
					logger.debug("Executing TopicCommand RollupMetrics @ MgmtClient, Topic:"+this.topic.getTopicName()+",Namespace:"+this.topic.getNamespaceName());
					
					client.executeMetricsCommand(clRequest);
					
					if((clRequest.getResponse()!=null) && (clRequest.getResponse() instanceof MetricValueSetCollection)) {
						populateTopicRollupMetrics(clRequest.getResponse());
					} else {
						commandSuccessful = false;
						throw new Exception("Unable to retrieve the Topic Rollup Metrics @ MgmtClient for Topic:"+this.topic.getTopicName()+",namespaceName:"+
								this.topic.getNamespaceName()+", ResourceGroupName:"+this.topic.getResourceGroupName());						
					}
					
				} else if(getMetricsType() == MetricsType.CONFIG_METRICS) {
					
					ClientRequest<String> clRequest = getClientRequestForSBClientTopicConfigMetrics(getRequestUriForSBClientTopicConfigMetrics());
					
					logger.debug("Executing TopicCommand ConfigMetrics @ SBClient, Topic:"+this.topic.getTopicName()+",Namespace:"+this.topic.getNamespaceName());
					
					client.executeMetricsCommand(clRequest);
					
					if(clRequest.getResponse()!=null) {
						populateTopicConfigMetrics(clRequest.getResponse());
					} else {
						commandSuccessful = false;
						throw new Exception("Unable to retrieve the Topic Config Metrics @ ServiceBusClient for Topic:"+this.topic.getTopicName()+",namespaceName:"+
								this.topic.getNamespaceName()+", ResourceGroupName:"+this.topic.getResourceGroupName());						
					}
				}
				
			} else {
				
				if(client instanceof ServiceBusClient) {
					
					ClientRequest<String> clRequest = getClientRequestForSBClientTopicConfig(getRequestUriForSBClientTopicConfig());
					
					logger.debug("Executing TopicCommand Config @ SBClient, Topic:"+this.topic.getTopicName()+",Namespace:"+this.topic.getNamespaceName());
					client.executeConfigCommand(clRequest);
					
					if(clRequest.getResponse()!=null) {
						this.topicCmdResult = (V) AppContext.getJaxbUtils().getObjectFromXml(clRequest.getResponse(), TopicDescription.class);
					} else {
						commandSuccessful = false;
						throw new Exception("Unable to retrieve the Topic Configuration @ ServiceBusClient for Topic:"+this.topic.getTopicName()+",namespaceName:"+
								this.topic.getNamespaceName()+", ResourceGroupName:"+this.topic.getResourceGroupName());						
					}

				} else if(client instanceof MgmtClient) {
					
					ClientRequest<String> clRequest = getClientRequestForMgmtClientTopicConfig(getRequestUriForMgmtClientTopicConfig());
					logger.debug("Executing TopicCommand Config @ MgmtClient, Topic:"+this.topic.getTopicName()+",Namespace:"+this.topic.getNamespaceName());
					
					client.executeConfigCommand(clRequest);
					
					if(clRequest.getResponse()!=null) {
						this.topicCmdResult = (V) AppContext.getJaxbUtils().getObjectFromXml(clRequest.getResponse(), TopicDescription.class);
					} else {
						commandSuccessful = false;
						throw new Exception("Unable to retrieve the Topic Configuration @ MgmtClient for Topic:"+this.topic.getTopicName()+",namespaceName:"+
									this.topic.getNamespaceName()+", ResourceGroupName:"+this.topic.getResourceGroupName());
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
				logger.error("Caught Exeption While Executing TopicCommand for Metrics", e);
				throw new MetricsException("Caught Exeption While Executing TopicCommand for Metrics", e);
			} else {
				logger.error("Caught Exeption While Executing TopicCommand for Config", e);
				throw new ConfigException("Caught Exeption While Executing TopicCommand for Config", e);
			}			
			
		}
		
		
	}
	
	private void populateDefaultValuesForBooleans() {
		TopicDescription td = (TopicDescription)this.topicCmdResult;
		if(td.isEnableBatchedOperations()==null) {
			td.setEnableBatchedOperations(Boolean.FALSE);
		}
		if(td.isEnableExpress()==null) {
			td.setEnableExpress(Boolean.FALSE);
		}
		if(td.isEnablePartitioning()==null) {
			td.setEnablePartitioning(Boolean.FALSE);
		}
		if(td.isFilteringMessagesBeforePublishing()==null) {
			td.setFilteringMessagesBeforePublishing(Boolean.FALSE);
		}
		if(td.isIsAnonymousAccessible()==null) {
			td.setIsAnonymousAccessible(Boolean.FALSE);
		}
		if(td.isRequiresDuplicateDetection()==null) {
			td.setRequiresDuplicateDetection(Boolean.FALSE);
		}
		if(td.isSupportOrdering()==null) {
			td.setSupportOrdering(Boolean.FALSE);
		}
	}
	
	private void populateTopicConfigMetrics(String xmlResponse) throws Exception {

		TopicMetrics topicMetrics = (TopicMetrics) this.topicCmdResult;
		
		TopicDescription response = AppContext.getJaxbUtils().getObjectFromXml(xmlResponse, TopicDescription.class);
		
		if(response!=null && response.getCountDetails()!=null) {
			
			topicMetrics.setActiveMessageCount(response.getCountDetails().getActiveMessageCount().intValue());
			topicMetrics.setDeadLetterMessageCount(response.getCountDetails().getDeadLetterMessageCount().intValue());
			topicMetrics.setScheduledMessageCount(response.getCountDetails().getScheduledMessageCount().intValue());
			topicMetrics.setTransferMessageCount(response.getCountDetails().getTransferDeadLetterMessageCount().intValue());
			topicMetrics.setTransferMessageCount(response.getCountDetails().getTransferMessageCount().intValue());
		
		}
		
	}

	private void populateTopicRollupMetrics(MetricValueSetCollection response) {
		
		TopicMetrics topicMetrics = (TopicMetrics)this.topicCmdResult;
		
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
				
				setMetricsProperty(topicMetrics, metricName, total);
			}
		}
		
		Timespan ts = new Timespan();
		ts.setStartTime(startTime);
		ts.setEndTime(endTime);
		topicMetrics.setTimespan(ts);
		topicMetrics.setTimeInterval(TimeGrain.PT_5_M.value());
		topicMetrics.setTopicName(this.topic.getTopicName());
		topicMetrics.setNamespaceName(this.topic.getNamespaceName());
		topicMetrics.setResourceGroupName(this.topic.getResourceGroupName());
		
	}
	
	private void setMetricsProperty(TopicMetrics subMetrics, String metricName, int total) {
		
		if(metricName.equalsIgnoreCase("size")) {
			subMetrics.setSize(total);
		} else if(metricName.equalsIgnoreCase("incoming")) {
			subMetrics.setIncoming(total);
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


	private ClientRequest<String> getClientRequestForMgmtClientTopicConfig(String requestUri) {
		
		ClientRequest<String> clRequest = new ClientRequest<String>();
		clRequest.setUri(requestUri);
		for(Entry<String,String> entry: MgmtApiConstants.TOPIC_CONFIG_HEADERS.entrySet()) {
			clRequest.getHeaders().setProperty(entry.getKey(), entry.getValue());
		}
		clRequest.setBody(null);
		clRequest.setRequestMethod(MgmtApiConstants.TOPIC_CONFIG_REQUEST_METHOD);
		clRequest.setEntityName(this.topic.getTopicName());
		clRequest.setEntityType(EntityType.TOPIC);
		clRequest.setNamespaceName(this.topic.getNamespaceName());
		clRequest.setResourceGroupName(this.topic.getResourceGroupName());
		clRequest.setResponseClass(String.class);
		
		return clRequest;
	}


	private String getRequestUriForSBClientTopicConfig() {
		//"https://{namespaceName}.servicebus.windows.net/{topicName}?api-version={api-version}"
		String uri = ServiceBusApiConstants.TOPIC_CONFIG_URI;
		
		uri = uri.replace(ConfigConstants.REPLACE_NAMESPACE_NAME, this.topic.getNamespaceName());
		uri = uri.replace(ConfigConstants.REPLACE_TOPIC_NAME, this.topic.getTopicName());
		uri = uri.replace(ConfigConstants.REPLACE_API_VERSION, ServiceBusApiConstants.TOPIC_CONFIG_API_VERSION);
		
		return uri;
	}


	private ClientRequest<String> getClientRequestForSBClientTopicConfig(String requestUri) {
		
		ClientRequest<String> clRequest = new ClientRequest<String>();
		clRequest.setUri(requestUri);
		clRequest.setBody(null);

		for(Entry<String,String> entry: ServiceBusApiConstants.TOPIC_CONFIG_HEADERS.entrySet()) {
			clRequest.getHeaders().setProperty(entry.getKey(), entry.getValue());
		}
		String sasToken = this.topic.getSasTokenNamespaceRootKey();
		clRequest.getHeaders().setProperty(ConfigConstants.AUTHORIZATION_HEADER_NAME, sasToken);
		
		clRequest.setRequestMethod(ServiceBusApiConstants.TOPIC_CONFIG_REQUEST_METHOD);
		clRequest.setEntityName(this.topic.getTopicName());
		clRequest.setEntityType(EntityType.TOPIC);
		clRequest.setNamespaceName(this.topic.getNamespaceName());
		clRequest.setResourceGroupName(this.topic.getResourceGroupName());
		clRequest.setResponseClass(String.class);

		return clRequest;
	}


	private String getRequestUriForMgmtClientTopicConfig() {
		
		//"https://management.core.windows.net/{tenantId}/services/ServiceBus/Namespaces/{namespaceName}/topics/{topicName}"
		String uri = MgmtApiConstants.TOPIC_CONFIG_URL;

		uri = uri.replace(ConfigConstants.REPLACE_TENANT_ID, AppContext.getDefaultTenantId());
		uri = uri.replace(ConfigConstants.REPLACE_NAMESPACE_NAME, this.topic.getNamespaceName());
		uri = uri.replace(ConfigConstants.REPLACE_TOPIC_NAME, this.topic.getTopicName());

		return uri;
	}


	public ClientRequest<MetricValueSetCollection> getClientRequestForMgmtClientTopicRollupMetrics(String uri) {
		
		ClientRequest<MetricValueSetCollection> clRequest = new ClientRequest<MetricValueSetCollection>();
		clRequest.setUri(uri);
		clRequest.setBody(null);
		for(Entry<String,String> entry: MgmtApiConstants.TOPIC_ROLLUP_METRICS_HEADERS.entrySet()) {
			clRequest.getHeaders().setProperty((String)entry.getKey(), (String)entry.getValue());
		}
		clRequest.setRequestMethod(MgmtApiConstants.TOPIC_ROLLUP_METRICS_REQUEST_METHOD);
		
		clRequest.setEntityName(this.topic.getTopicName());
		clRequest.setEntityType(EntityType.TOPIC);
		clRequest.setNamespaceName(this.topic.getNamespaceName());
		clRequest.setResourceGroupName(this.topic.getResourceGroupName());		
		clRequest.setResponseClass(MetricValueSetCollection.class);

		return clRequest;		
	}
	
	public String getRequestUriForMgmtClientTopicRollupMetrics() {
		//https://management.core.windows.net/{subscriptionId}/services/monitoring/metricvalues/query?resourceId=/ServiceBus/Namespaces/
		//{namespaceName}/Topics/{topicName}&names={topicMetricsNames}&timeGrain={timeGrain}&startTime={startTime}&endTime={endTime}
		String uri = MgmtApiConstants.TOPIC_ROLLUP_METRICS_URL;
		
		uri = uri.replace(ConfigConstants.REPLACE_SUBSCRIPTION_ID, AppContext.getDefaultSubscriptionId());
		uri = uri.replace(ConfigConstants.REPLACE_NAMESPACE_NAME, topic.getNamespaceName());
		uri = uri.replace(ConfigConstants.REPLACE_TOPIC_NAME, topic.getTopicName());
		uri = uri.replace(ConfigConstants.REPLACE_METRIC_NAMES, MgmtApiConstants.TOPIC_ROLLUP_METRICS_NAMES);
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
	
	public ClientRequest<String> getClientRequestForSBClientTopicConfigMetrics(String uri) {
		
		ClientRequest<String> clRequest = new ClientRequest<String>();
		clRequest.setUri(uri);
		clRequest.setBody(null);
		clRequest.setRequestMethod(HttpMethod.GET);
		
		for(Entry<String,String> entry: ServiceBusApiConstants.TOPIC_CONFIG_HEADERS.entrySet()) {
			clRequest.getHeaders().setProperty(entry.getKey(), entry.getValue());
		}

		String sasToken = this.topic.getSasTokenNamespaceRootKey();
		clRequest.getHeaders().setProperty(ConfigConstants.AUTHORIZATION_HEADER_NAME, sasToken);
		
		clRequest.setEntityName(this.topic.getTopicName());
		clRequest.setEntityType(EntityType.TOPIC);
		clRequest.setNamespaceName(this.topic.getNamespaceName());
		clRequest.setResourceGroupName(this.topic.getResourceGroupName());
		clRequest.setResponseClass(String.class);
		
		return clRequest;
	}
	
	public String getRequestUriForSBClientTopicConfigMetrics() {

		String uri = ServiceBusApiConstants.TOPIC_CONFIG_URI;
		uri = uri.replace(ConfigConstants.REPLACE_NAMESPACE_NAME, topic.getNamespaceName());
		uri = uri.replace(ConfigConstants.REPLACE_TOPIC_NAME, topic.getTopicName());
		uri = uri.replace(ConfigConstants.REPLACE_API_VERSION, ServiceBusApiConstants.TOPIC_CONFIG_API_VERSION);
		
		return uri;
	}	

}
