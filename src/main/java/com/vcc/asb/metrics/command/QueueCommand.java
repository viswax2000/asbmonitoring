package com.vcc.asb.metrics.command;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vcc.asb.config.model.QueueDescription;
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
import com.vcc.asb.metrics.model.QueueMetrics;
import com.vcc.asb.metrics.model.TimeGrain;
import com.vcc.asb.metrics.model.Timespan;
import com.vcc.asb.model.Queue;
import com.vcc.asb.service.Client;
import com.vcc.asb.service.ClientRequest;
import com.vcc.asb.service.MgmtClient;
import com.vcc.asb.service.ServiceBusClient;
import com.vcc.asb.util.JAXBUtils;
/**
 * Information on which APIs to invoke to get the metrics
 * 1. Rollup Metrics - Management API --> Certificate Authentication
 * 2. Message Count Metrics - Management API --> Certificate Authentication
 * 
 * @author HCL Technologies AB
 *
 */
public class QueueCommand<V> extends BaseCommand<V> {
	
	private Queue queue;
	private V queueCmdResult;
	
	private static Logger logger = LoggerFactory.getLogger(QueueCommand.class);
	
	public QueueCommand(Queue queue) {
		this.queue = queue;
		if(BatchContext.getCurrentBatchType() == BatchType.METRICS) {
			this.queueCmdResult = (V) new QueueMetrics();
		} else if(BatchContext.getCurrentBatchType() == BatchType.CONFIG) {
			this.queueCmdResult = (V) new QueueDescription();
		}
	}
	
	public Queue getEntity() {
		return this.queue;
	}
	
	public EntityType getEntityType() {
		return EntityType.QUEUE;
	}
	
	public V getCommandResult() {
		return this.queueCmdResult;
	}
	
	public List<MetricsType> getMetricsTypes() {
		return Arrays.asList(MetricsType.ROLLUP_METRICS, MetricsType.CONFIG_METRICS);
	}

	/**
	 * This execn can be for both Metrics and Config. Under metrics there are RollUp, and Config
	 * So it needs to distinguish first if it is for Config, or Metrics, and if Metrics
	 * then is it for Rollup, or Config Metrics  
	 */
	@Override
	public void execute(Client client) {
		
		try {
		
			if(BatchContext.getCurrentBatchType() == BatchType.METRICS) {
				
				if(getMetricsType() == MetricsType.ROLLUP_METRICS) {
					
					ClientRequest<MetricValueSetCollection> clRequest = getClientRequestForMgmtClientQueueRollupMetrics(getRequestUriForMgmtClientQueueRollupMetrics());
					
					logger.debug("Executing QueueCommand RollupMetrics @ MgmtClient, Queue:"+this.queue.getQueueName()+",Namespace:"+this.queue.getNamespaceName());
					
					client.executeMetricsCommand(clRequest);
					
					if((clRequest.getResponse()!=null) && (clRequest.getResponse() instanceof MetricValueSetCollection)) {
						populateQueueRollupMetrics(clRequest.getResponse());
					} else {
						commandSuccessful = false;
						throw new Exception("Unable to retrieve the Queue Rollup Metrics @ MgmtClient for Queue:"+this.queue.getQueueName()+",namespaceName:"+
								this.queue.getNamespaceName()+", ResourceGroupName:"+this.queue.getResourceGroupName());							
					}
					
				} else if(getMetricsType() == MetricsType.CONFIG_METRICS) {
					
					ClientRequest<String> clRequest = getClientRequestForSBClientQueueConfigMetrics(getRequestUriForSBClientQueueConfigMetrics());
					logger.debug("Executing QueueCommand ConfigMetrics @ SBClient, Queue:"+this.queue.getQueueName()+",Namespace:"+this.queue.getNamespaceName());
					
					client.executeMetricsCommand(clRequest);
					
					if(clRequest.getResponse()!=null) {
						populateQueueConfigMetrics(clRequest.getResponse());
					} else {
						commandSuccessful = false;
						throw new Exception("Unable to retrieve the Queue Config Metrics @ ServiceBusClient for Queue:"+this.queue.getQueueName()+",namespaceName:"+
								this.queue.getNamespaceName()+", ResourceGroupName:"+this.queue.getResourceGroupName());						
					}
					
				}
				
			} else {
				
				if(client instanceof ServiceBusClient) {
					
					ClientRequest<String> clRequest = getClientRequestForSBClientQueueConfig(getRequestUriForServiceBusClientQueueConfig());
					
					logger.debug("Executing QueueCommand Config @ SBClient, Queue:"+this.queue.getQueueName()+",Namespace:"+this.queue.getNamespaceName());

					client.executeConfigCommand(clRequest);
					
					if(clRequest.getResponse()!=null) {
						this.queueCmdResult = (V) AppContext.getJaxbUtils().getObjectFromXml(clRequest.getResponse(), QueueDescription.class);
					} else {
						commandSuccessful = false;
						throw new Exception("Unable to retrieve the Queue Config @ ServiceBusClient for Queue:"+this.queue.getQueueName()+",namespaceName:"+
								this.queue.getNamespaceName()+", ResourceGroupName:"+this.queue.getResourceGroupName());						
						
					}
					
				} else if(client instanceof MgmtClient) {
					
					ClientRequest<String> clRequest = getClientRequestForMgmtClientQueueConfig(getRequestUriForMgmtClientQueueConfig());
					
					logger.debug("Executing QueueCommand Config @ MgmtClient, Queue:"+this.queue.getQueueName()+",Namespace:"+this.queue.getNamespaceName());

					client.executeConfigCommand(clRequest);
					
					if(clRequest.getResponse()!=null) {
						this.queueCmdResult = (V) AppContext.getJaxbUtils().getObjectFromXml(clRequest.getResponse(), QueueDescription.class);
					} else {
						commandSuccessful = false;
						throw new Exception("Unable to retrieve the Queue Config @ MgmtClient for Queue:"+this.queue.getQueueName()+",namespaceName:"+
								this.queue.getNamespaceName()+", ResourceGroupName:"+this.queue.getResourceGroupName());						
						
					}
				}
				
				logger.debug("Populating Default values for Boolean Nulls");
				
				populateDefaultValuesForBooleans();
			}
			
			this.commandSuccessful = true;
			
		} catch(Exception e) {
			this.commandException = e;
			this.commandSuccessful = false;
			
			if(BatchContext.getCurrentBatchType() == BatchType.METRICS) {
				logger.error("Caught Exception while Executing QueueCommand for Metrics",e);
				throw new MetricsException("Caught Exception while Executing QueueCommand For Metrics", e);
			} else {
				logger.error("Caught Exception while Executing QueueCommand for Config",e);
				throw new ConfigException("Caught Exception while Executing QueueCommand for Config", e);
			}
		}

	}
	
	private void populateDefaultValuesForBooleans() {
		
		QueueDescription qd = (QueueDescription)this.queueCmdResult;
		if(qd.isDeadLetteringOnMessageExpiration()==null) {
			qd.setDeadLetteringOnMessageExpiration(Boolean.FALSE);
		}
		if(qd.isEnableBatchedOperations() == null) {
			qd.setEnableBatchedOperations(Boolean.FALSE);
		}
		if(qd.isEnableExpress() == null) {
			qd.setEnableExpress(Boolean.FALSE);
		}
		if(qd.isEnablePartitioning() == null) {
			qd.setEnablePartitioning(Boolean.FALSE);
		}
		
		if(qd.isIsAnonymousAccessible() == null) {
			qd.setIsAnonymousAccessible(Boolean.FALSE);
		}
		if(qd.isRequiresDuplicateDetection()==null) {
			qd.setRequiresSession(Boolean.FALSE);
		}
		if(qd.isRequiresSession()==null) {
			qd.setRequiresSession(Boolean.FALSE);
		}
		if(qd.isSupportOrdering() == null) {
			qd.setSupportOrdering(Boolean.FALSE);
		}
	}
	
	private void populateQueueConfigMetrics(String response) throws Exception {

		logger.debug("Populating Queue ConfigMetrics, Queue:"+this.queue.getQueueName()+", NamespaceName:"+this.queue.getNamespaceName());
		
		QueueMetrics queueMetrics = (QueueMetrics)this.queueCmdResult;
		
		JAXBUtils jaxbUtils = AppContext.getJaxbUtils();
		
		QueueDescription qd = jaxbUtils.getObjectFromXml(response, QueueDescription.class);
		
		if(qd!=null && qd.getCountDetails()!=null) {
			queueMetrics.setActiveMessageCount(qd.getCountDetails().getActiveMessageCount().intValue());
			queueMetrics.setDeadLetterMessageCount(qd.getCountDetails().getDeadLetterMessageCount().intValue());
			queueMetrics.setScheduledMessageCount(qd.getCountDetails().getScheduledMessageCount().intValue());
			queueMetrics.setTransferDeadLetterMessageCount(qd.getCountDetails().getTransferDeadLetterMessageCount().intValue());
			queueMetrics.setTransferMessageCount(qd.getCountDetails().getTransferMessageCount().intValue());
		}
		
		logger.debug("Populated Queue ConfigMetrics Successfully");
		
	}

	private void populateQueueRollupMetrics(MetricValueSetCollection response) {
		
		logger.debug("Populating QueueRollupMetrics, Queue:"+this.queue.getQueueName()+", NamespaceName:"+this.queue.getNamespaceName());

		QueueMetrics queueMetrics = (QueueMetrics)this.queueCmdResult;
		
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
				
				setMetricsProperty(queueMetrics, metricName, total);
			}
		}
		
		Timespan ts = new Timespan();
		ts.setStartTime(startTime);
		ts.setEndTime(endTime);
		queueMetrics.setTimespan(ts);
		queueMetrics.setTimeInterval(TimeGrain.PT_5_M.value());
		queueMetrics.setQueueName(this.queue.getQueueName());
		queueMetrics.setNamespaceName(this.queue.getNamespaceName());
		queueMetrics.setResourceGroupName(this.queue.getResourceGroupName());
		
		logger.debug("Populated Queue RollupMetrics Successfully");

	}
	
	private void setMetricsProperty(QueueMetrics queueMetrics, String metricName, int total) {
		
		if(metricName.equalsIgnoreCase("size")) {
			queueMetrics.setSize(total);
		} else if(metricName.equalsIgnoreCase("incoming")) {
			queueMetrics.setIncoming(total);
		} else if(metricName.equalsIgnoreCase("outgoing")) {
			queueMetrics.setOutgoing(total);
		} else if(metricName.equalsIgnoreCase("length")) {			
			queueMetrics.setLength(total);
		} else if(metricName.equalsIgnoreCase("requests.successful")) {
			queueMetrics.setRequestsSuccessful(total);
		} else if(metricName.equalsIgnoreCase("requests.total")) {
			queueMetrics.setRequestsTotal(total);
		} else if(metricName.equalsIgnoreCase("requests.failed")) {
			queueMetrics.setRequestsFailed(total);
		} else if(metricName.equalsIgnoreCase("requests.failed.serverbusy")) {
			queueMetrics.setRequestsFailedServerbusy(total);
		} else if(metricName.equalsIgnoreCase("requests.failed.other")) {
			queueMetrics.setRequestsFailedOther(total);
		} else if(metricName.equalsIgnoreCase("requests.failed.internalservererror")) {
			queueMetrics.setRequestsFailedInternalservererror(total);
		}

	}	

	private ClientRequest<String> getClientRequestForMgmtClientQueueConfig(String requestUri) {
		
		ClientRequest<String> clRequest = new ClientRequest<String>();
		clRequest.setUri(requestUri);
		clRequest.setBody(null);
		
		for(Entry<String,String> entry: MgmtApiConstants.QUEUE_CONFIG_HEADERS.entrySet()) {
			clRequest.getHeaders().setProperty(entry.getKey(), entry.getValue());
		}
		clRequest.setRequestMethod(MgmtApiConstants.QUEUE_CONFIG_REQUEST_METHOD);
		
		clRequest.setEntityName(this.queue.getQueueName());
		clRequest.setEntityType(EntityType.QUEUE);
		clRequest.setNamespaceName(this.queue.getNamespaceName());
		clRequest.setResourceGroupName(this.queue.getResourceGroupName());
		clRequest.setResponseClass(String.class);
		
		return clRequest;
	}

	private String getRequestUriForMgmtClientQueueConfig() {
		//"https://management.core.windows.net/{tenantId}/services/ServiceBus/Namespaces/{namespaceName}/queues/{queueName}"
		String uri = MgmtApiConstants.QUEUE_CONFIG_URL;
		uri = uri.replace(ConfigConstants.REPLACE_TENANT_ID, AppContext.getDefaultTenantId());
		uri = uri.replace(ConfigConstants.REPLACE_NAMESPACE_NAME, this.queue.getNamespaceName());
		uri = uri.replace(ConfigConstants.REPLACE_QUEUE_NAME, this.queue.getQueueName());
		
		return uri;
	}

	private ClientRequest<String> getClientRequestForSBClientQueueConfig(String requestUri) {
		
		ClientRequest<String> clRequest = new ClientRequest<String>();
		clRequest.setUri(requestUri);
		clRequest.setBody(null);
		
		for(Entry<String,String> entry: ServiceBusApiConstants.QUEUE_CONFIG_HEADERS.entrySet()) {
			clRequest.getHeaders().setProperty(entry.getKey(), entry.getValue());
		}
		String sasToken = this.queue.getSasTokenNamespaceRootKey();
		clRequest.getHeaders().setProperty(ConfigConstants.AUTHORIZATION_HEADER_NAME, sasToken);
		
		clRequest.setEntityName(this.queue.getQueueName());
		clRequest.setEntityType(EntityType.QUEUE);
		clRequest.setNamespaceName(this.queue.getNamespaceName());
		clRequest.setResourceGroupName(this.queue.getResourceGroupName());
		clRequest.setResponseClass(String.class);
		
		return clRequest;
	}

	private String getRequestUriForServiceBusClientQueueConfig() {
		//"https://{namespaceName}.servicebus.windows.net/{queueName}?api-version={api-version}"
		String uri = ServiceBusApiConstants.QUEUE_CONFIG_URI;
		uri = uri.replace(ConfigConstants.REPLACE_NAMESPACE_NAME, this.queue.getNamespaceName());
		uri = uri.replace(ConfigConstants.REPLACE_QUEUE_NAME, this.queue.getQueueName());
		uri = uri.replace(ConfigConstants.REPLACE_API_VERSION, ServiceBusApiConstants.QUEUE_CONFIG_API_VERSION);
		return uri;
	}

	public ClientRequest<MetricValueSetCollection> getClientRequestForMgmtClientQueueRollupMetrics(String uri) {
		
		ClientRequest<MetricValueSetCollection> clRequest = new ClientRequest<MetricValueSetCollection>();
		clRequest.setUri(uri);
		clRequest.setBody(null);
		for(Entry<String,String> entry: MgmtApiConstants.QUEUE_ROLLUP_METRICS_HEADERS.entrySet()) {
			clRequest.getHeaders().setProperty(entry.getKey(), entry.getValue());
		}
		clRequest.setRequestMethod(MgmtApiConstants.QUEUE_ROLLUP_METRICS_REQUEST_METHOD);
		
		clRequest.setEntityName(this.queue.getQueueName());
		clRequest.setEntityType(EntityType.QUEUE);
		clRequest.setNamespaceName(this.queue.getNamespaceName());
		clRequest.setResourceGroupName(this.queue.getResourceGroupName());
		clRequest.setResponseClass(MetricValueSetCollection.class);		

		return clRequest;
	}
	
	public String getRequestUriForMgmtClientQueueRollupMetrics() {
		
		String uri = MgmtApiConstants.QUEUE_ROLLUP_METRICS_URL;
		uri = uri.replace(ConfigConstants.REPLACE_SUBSCRIPTION_ID, AppContext.getDefaultSubscriptionId());
		uri = uri.replace(ConfigConstants.REPLACE_NAMESPACE_NAME, queue.getNamespaceName());
		uri = uri.replace(ConfigConstants.REPLACE_QUEUE_NAME, queue.getQueueName());
		uri = uri.replace(ConfigConstants.REPLACE_METRIC_NAMES, MgmtApiConstants.QUEUE_ROLLUP_METRICS_NAMES);
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
	
	public ClientRequest<String> getClientRequestForSBClientQueueConfigMetrics(String uri) {
		
		ClientRequest<String> clRequest = new ClientRequest<String>();
		clRequest.setUri(uri);
		clRequest.setBody(null);
		clRequest.setRequestMethod(ServiceBusApiConstants.QUEUE_CONFIG_REQUEST_METHOD);
		
		for(Entry<String,String> entry: ServiceBusApiConstants.QUEUE_CONFIG_HEADERS.entrySet()) {
			clRequest.getHeaders().setProperty((String)entry.getKey(), (String)entry.getValue());
		}

		String sasToken = this.queue.getSasTokenNamespaceRootKey();
		clRequest.getHeaders().setProperty(ConfigConstants.AUTHORIZATION_HEADER_NAME, sasToken);
		
		clRequest.setEntityName(this.queue.getQueueName());
		clRequest.setEntityType(EntityType.QUEUE);
		clRequest.setNamespaceName(this.queue.getNamespaceName());
		clRequest.setResourceGroupName(this.queue.getResourceGroupName());
		clRequest.setResponseClass(String.class);
		
		return clRequest;
	}
	
	public String getRequestUriForSBClientQueueConfigMetrics() {

		String uri = ServiceBusApiConstants.QUEUE_CONFIG_URI;
		uri = uri.replace(ConfigConstants.REPLACE_NAMESPACE_NAME, queue.getNamespaceName());
		uri = uri.replace(ConfigConstants.REPLACE_QUEUE_NAME, queue.getQueueName());
		uri = uri.replace(ConfigConstants.REPLACE_API_VERSION, ServiceBusApiConstants.QUEUE_CONFIG_API_VERSION);
		
		return uri;
	}

}
