package com.vcc.asb.metrics.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vcc.asb.config.model.NamespaceDescription;
import com.vcc.asb.configuration.ConfigConstants;
import com.vcc.asb.configuration.EntityType;
import com.vcc.asb.configuration.MgmtApiConstants;
import com.vcc.asb.configuration.MonitorApiConstants;
import com.vcc.asb.context.AppContext;
import com.vcc.asb.context.BatchContext;
import com.vcc.asb.context.BatchType;
import com.vcc.asb.exception.ConfigException;
import com.vcc.asb.exception.MetricsException;
import com.vcc.asb.metrics.model.NamespaceMetrics;
import com.vcc.asb.metrics.model.TimeGrain;
import com.vcc.asb.metrics.model.Timespan;
import com.vcc.asb.model.Namespace;
import com.vcc.asb.service.AzureServiceClient;
import com.vcc.asb.service.Client;
import com.vcc.asb.service.ClientRequest;
import com.vcc.asb.service.MgmtClient;
import com.vcc.asb.util.MonitorMetricsParser;

public class NamespaceCommand<V> extends BaseCommand<V> {
	
	Namespace namespace = null;
	V nsCmdResult = null;
	
	private static Logger logger = LoggerFactory.getLogger(NamespaceCommand.class);
	
	public NamespaceCommand(Namespace ns) {
		this.namespace = ns;
		if(BatchContext.getCurrentBatchType() == BatchType.METRICS) {
			this.nsCmdResult = (V) new NamespaceMetrics();
		} else {
			this.nsCmdResult = (V) new NamespaceDescription();
		}
	}

	@Override
	public List<MetricsType> getMetricsTypes() {
		return Arrays.asList(MetricsType.NAMESPACE_METRICS);
	}
	
	public EntityType getEntityType() {
		return EntityType.NAMESPACE;
	}
	
	public Namespace getEntity() {
		return this.namespace;
	}

	@Override
	public void execute(Client client) {
		
		try {

			if(BatchContext.getCurrentBatchType() == BatchType.METRICS) {

				if(getMetricsType() == MetricsType.NAMESPACE_METRICS) {
					
					ClientRequest<ObjectNode> clRequest = null;
				
					clRequest = getClientRequestForNamespaceMonitorMetrics(getRequestUriForNamespaceMonitorMetrics(MonitorApiConstants.NAMESPACE_METRICS_NAMES_1));
					logger.debug("Executing NamespaceCommand @ MonitorClient, Namespace:"+this.namespace.getNamespaceName()+", for Metrics Names 1");
					
					client.executeMetricsCommand(clRequest);
					
					if(clRequest.getResponse()!=null) {
						logger.debug("Parsing Monitor Metrics for NamespaceCommand for Metrics Names 1");
						HashMap<String,Integer> metrics1 = MonitorMetricsParser.parseNamespaceMetrics(clRequest.getResponse());
						populateNamespaceMonitorMetrics(metrics1, ((NamespaceMetrics)nsCmdResult));
					} else {
						commandSuccessful = false;
						throw new Exception("Unable to retrieve the Namespace Metrics1 @ MonitorClient for namespaceName:"+
								this.namespace.getNamespaceName()+", ResourceGroupName:"+this.namespace.getResourceGroupName());						
					}
					
					clRequest = getClientRequestForNamespaceMonitorMetrics(getRequestUriForNamespaceMonitorMetrics(MonitorApiConstants.NAMESPACE_METRICS_NAMES_2));
					logger.debug("Executing NamespaceCommand @ MonitorClient, Namespace:"+this.namespace.getNamespaceName()+", for Metrics Names 2");
					
					client.executeMetricsCommand(clRequest);
					
					if(clRequest.getResponse()!=null) {
						logger.debug("Parsing Monitor Metrics for NamespaceCommand for Metrics Names 2");
						HashMap<String,Integer> metrics2 = MonitorMetricsParser.parseNamespaceMetrics(clRequest.getResponse());
						populateNamespaceMonitorMetrics(metrics2, ((NamespaceMetrics)nsCmdResult));
					} else {
						commandSuccessful = false;
						throw new Exception("Unable to retrieve the Namespace Metrics2 @ MonitorClient for namespaceName:"+
								this.namespace.getNamespaceName()+", ResourceGroupName:"+this.namespace.getResourceGroupName());						
					}
				}
				
			} else {
				
				ClientRequest<NamespaceDescription> clRequest = null;
				
				if(client instanceof AzureServiceClient) {
					
					clRequest = getClientRequestForAzureServiceClientNamespaceConfig(getRequestUriForAzureServiceClientNamespaceConfig());
					
					logger.debug("Executing NamespaceCommand for Config @ AzureServiceClient, Namespace:"+this.namespace.getNamespaceName());
					client.executeConfigCommand(clRequest);
					
					if((clRequest.getResponse()!=null) && (clRequest.getResponse() instanceof NamespaceDescription)) {
						this.nsCmdResult = (V) clRequest.getResponse();
					} else {
						commandSuccessful = false;
						throw new Exception("Unable to retrieve the Namespace Config @ AzureServiceClient for Namespace:"+this.namespace.getNamespaceName()+
								", ResourceGroupName:"+this.namespace.getResourceGroupName());						
					}
					
				} else if(client instanceof MgmtClient) {

					clRequest = getClientRequestForMgmtClientNamespaceConfig(getRequestUriForMgmtClientNamespaceConfig());
					
					logger.debug("Executing NamespaceCommand for Config @ MgmtClient, Namespace:"+this.namespace.getNamespaceName());
					client.executeConfigCommand(clRequest);
					
					if((clRequest.getResponse()!=null) && (clRequest.getResponse() instanceof NamespaceDescription)) {
						this.nsCmdResult = (V) clRequest.getResponse();
					} else {
						commandSuccessful = false;
						throw new Exception("Unable to retrieve the Namespace Config @ MgmtClient for Namespace:"+this.namespace.getNamespaceName()+
								", ResourceGroupName:"+this.namespace.getResourceGroupName());						
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
				logger.error("Caught Exeption While Executing NamespaceCommand for Metrics", e);
				throw new MetricsException("Caught Exeption While Executing NamespaceCommand for Metrics", e);
			} else {
				logger.error("Caught Exeption While Executing NamespaceCommand for Config", e);
				throw new ConfigException("Caught Exeption While Executing NamespaceCommand for Config", e);
			}	
		}

	}
	
	private void populateDefaultValuesForBooleans() {
		NamespaceDescription nd = (NamespaceDescription)this.nsCmdResult;
		if(nd.isCritical()==null) {
			nd.setCritical(Boolean.FALSE);
		}
		if(nd.isEnabled()==null) {
			nd.setEnabled(Boolean.FALSE);
		}
	}
	
	private ClientRequest<NamespaceDescription> getClientRequestForMgmtClientNamespaceConfig(String requestUri) {
		
		ClientRequest<NamespaceDescription> clRequest = new ClientRequest<NamespaceDescription>();
		
		clRequest.setUri(requestUri);
		clRequest.setBody(null);
		
		clRequest.setEntityName(this.namespace.getNamespaceName());
		clRequest.setEntityType(EntityType.NAMESPACE);
		clRequest.setRequestMethod(MgmtApiConstants.NAMESPACE_CONFIG_REQUEST_METHOD);
		clRequest.setResourceGroupName(this.namespace.getResourceGroupName());
		clRequest.setNamespaceName(this.namespace.getNamespaceName());
		
		for(Entry<String,String> entry: MgmtApiConstants.NAMESPACE_CONFIG_HEADERS.entrySet()) {
			clRequest.getHeaders().setProperty(entry.getKey(), entry.getValue());
		}
		
		return clRequest;
	}

	private String getRequestUriForMgmtClientNamespaceConfig() {

		//"https://management.core.windows.net/{tenantId}/services/ServiceBus/Namespaces/{namespaceName}"
		String uri = MgmtApiConstants.NAMESPACE_CONFIG_URL;
		uri = uri.replace("{tenantId}", AppContext.getDefaultTenantId());
		uri = uri.replace("{namespaceName}", this.namespace.getNamespaceName());
		
		return uri;
	}

	//Will not be a real API call, but a library method call through Azure API, so ClientRequest here
	//will be just a placeholder with some values like EntityName, EntityType, ResourceGroupName which
	//will be used in the AzureServiceClient.
	private ClientRequest<NamespaceDescription> getClientRequestForAzureServiceClientNamespaceConfig(String requestUri) {
		
		ClientRequest<NamespaceDescription> clRequest = new ClientRequest<NamespaceDescription>();

		clRequest.setBody(null);
		clRequest.setResponseClass(NamespaceDescription.class);
		clRequest.setRequestMethod(HttpMethod.GET);

		clRequest.setEntityName(this.namespace.getNamespaceName());
		clRequest.setEntityType(EntityType.NAMESPACE);
		clRequest.setResourceGroupName(this.namespace.getResourceGroupName());
		clRequest.setNamespaceName(this.namespace.getNamespaceName());

		return clRequest;
	}

	//we dont have the request uri .. but just as a convention we have this method
	private String getRequestUriForAzureServiceClientNamespaceConfig() {
		return ConfigConstants.BLANK;
	}

	public ClientRequest<ObjectNode> getClientRequestForNamespaceMonitorMetrics(String uri) {
		
		ClientRequest<ObjectNode> request = new ClientRequest<ObjectNode>();
		request.setUri(uri);
		request.setBody(null);
		request.setRequestMethod(MonitorApiConstants.NAMESPACE_METRICS_REQUEST_METHOD);
		request.getHeaders().setProperty("Authorization", "Bearer "+BatchContext.getMetricsContext().getAADAccessToken());
		request.getHeaders().setProperty("Accept", "application/json");
		//request.setResponseClass(String.class);
		request.setResponseClass(ObjectNode.class);
		
		request.setNamespaceName(this.namespace.getNamespaceName());
		request.setEntityName(this.namespace.getNamespaceName());
		request.setEntityType(EntityType.NAMESPACE);
		request.setResourceGroupName(this.namespace.getResourceGroupName());

		return request;
	}
	
	private String getRequestUriForNamespaceMonitorMetrics(String metricsNames) {
		
		String uri = MonitorApiConstants.NAMESPACE_METRICS_URL;
		
		uri = uri.replace("{subscriptionId}", AppContext.getDefaultSubscriptionId());
		uri = uri.replace("{resourceGroupName}", namespace.getResourceGroupName());
		uri = uri.replace("{namespaceName}", namespace.getNamespaceName());
		uri = uri.replace("{api-version}", MonitorApiConstants.NAMESPACE_METRICS_API_VERSION);
		
		uri = uri.replace("{metricsNames}", metricsNames);
		uri = uri.replace("{timegrain}", AppContext.getMonitorMetricsTimegrain());
		
		if(BatchContext.getLastRun()==null) {
			startTime = DateTime.now().minusMinutes(5);
			endTime = DateTime.now();
		
		} else {
			startTime = BatchContext.getLastRun();
			endTime = DateTime.now();
		}
		
		String stTime = startTime.toString(AppContext.getMonitorMetricsDateTimeFormat());
		String enTime = endTime.toString(AppContext.getMonitorMetricsDateTimeFormat());
		
		uri = uri.replace("{timespan}", stTime+"/"+enTime);
		
		//System.out.println(">>>>URI:"+uri);
		
		return uri;
		
	}

	private void populateNamespaceMonitorMetrics(HashMap<String,Integer> metrics, NamespaceMetrics nsMetrics) {
		logger.debug("Populating NamespaceCommand Monitor Metrics");
		for(Entry<String,Integer> entry: metrics.entrySet()) {
			String key = entry.getKey();
			try {
				nsMetrics.getClass().getDeclaredMethod("set"+key, Integer.class).invoke(nsMetrics, entry.getValue());
			} catch(Exception e) {
				e.printStackTrace();
			} 
		}
		
		Timespan ts = new Timespan();
		ts.setStartTime(startTime);
		ts.setEndTime(endTime);
		
		nsMetrics.setResourceGroupName(this.namespace.getResourceGroupName());
		nsMetrics.setNamespaceName(this.namespace.getNamespaceName());
		
		nsMetrics.setTimespan(ts);
		nsMetrics.setTimeInterval(TimeGrain.PT_5_M.value());
		logger.debug("Populated NamespaceCommand MonitorMetrics SUccessfully");
	}

	@Override
	public V getCommandResult() {
		return this.nsCmdResult;
	}
	
}
