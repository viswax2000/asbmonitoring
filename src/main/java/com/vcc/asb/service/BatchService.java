package com.vcc.asb.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.vcc.asb.config.model.NamespaceDescription;
import com.vcc.asb.config.model.QueueDescription;
import com.vcc.asb.config.model.SubscriptionDescription;
import com.vcc.asb.config.model.TopicDescription;
import com.vcc.asb.configuration.ServiceConfig;
import com.vcc.asb.context.AppContext;
import com.vcc.asb.context.BatchContext;
import com.vcc.asb.context.BatchType;
import com.vcc.asb.context.ConfigContext;
import com.vcc.asb.context.ConfigType;
import com.vcc.asb.context.MetricsCommandType;
import com.vcc.asb.context.MetricsContext;
import com.vcc.asb.exception.ConfigException;
import com.vcc.asb.exception.MetricsException;
import com.vcc.asb.metrics.command.Command;
import com.vcc.asb.metrics.command.NamespaceCommand;
import com.vcc.asb.metrics.command.QueueCommand;
import com.vcc.asb.metrics.command.SubscriptionCommand;
import com.vcc.asb.metrics.command.TopicCommand;
import com.vcc.asb.metrics.model.NamespaceMetrics;
import com.vcc.asb.metrics.model.QueueMetrics;
import com.vcc.asb.metrics.model.SubscriptionMetrics;
import com.vcc.asb.metrics.model.TopicMetrics;
import com.vcc.asb.model.ASBEntities;
import com.vcc.asb.model.Namespace;
import com.vcc.asb.model.Queue;
import com.vcc.asb.model.Subscription;
import com.vcc.asb.model.Topic;

@Service
public class BatchService {
	
	@Autowired
	AppInitializer appInitializer;
	
	@Autowired
	ServiceConfig serviceConfig;
	
	@Autowired
	MetricsService metricsService;
	
	@Autowired
	ConfigService configService;
	
	private static Logger logger = LoggerFactory.getLogger(BatchService.class);
	
	public BatchService() {
		
	}
	
	//@PostConstruct
	public void initialize() {
		logger.info(" *** Initializing the Application Context with ABS Entities *** ");
		appInitializer.initialize();
	}
	
	public void executeBatch(BatchType batchType) {
		
		logger.info("Initializing the contexts for BatchType "+batchType.name());
		this.initializeContexts(batchType);
		
		this.executeBatchCommands(batchType);
	}
	
	public void initializeContexts(BatchType batchType) {

		BatchContext.initBatchContext(batchType);
		
		if((AppContext.getAsbEntities()==null) || (batchType == BatchType.CONFIG)) {
			logger.info(" *** Initializing the Application Context again with ASB Entities *** ");
			appInitializer.initialize();
		}
		
		if(batchType == BatchType.METRICS) {
			initializeMetricsContext(BatchContext.getMetricsContext());
		} else {
			initializeConfigContext(BatchContext.getConfigContext());
		}
		
		logger.info("****** Batch Contexts initialized ******");
		
	}
	
	public void executeBatchCommands(BatchType batchType) {
		
		if(batchType == BatchType.METRICS) {
			MetricsContext metricsContext = BatchContext.getMetricsContext();
			
			for(MetricsCommandType mcType: MetricsCommandType.values()) {
				//****** REMOVE BELOW IF CHECK LATER ****** ONLY FOR TESTING PURPOSE *******/
				//if((mcType == MetricsCommandType.SUBSCRIPTION_COMMAND) || (mcType == MetricsCommandType.TOPIC_COMMAND)) {
					List<Command> commands = metricsContext.getMetricTypeCommands(mcType);
					logger.debug("Retrieved "+commands.size()+" "+mcType.getMetricsCommandClass().getSimpleName()+"Metric Commands");
					metricsService.executeMetricsCommands(commands);
				//}
			}
			
		} else if(batchType == BatchType.CONFIG) {
			ConfigContext configContext = BatchContext.getConfigContext();
			
			for(ConfigType ct: ConfigType.values()) {
				List<Command> commands = configContext.getConfigTypeCommands(ct);
				logger.debug("Retrieved "+commands.size()+" "+ct.name()+" Config commands");
				configService.executeConfigCommands(commands);
			}
		}
	}
	
	
	private void initializeMetricsContext(MetricsContext metricsContext) {

		try {
			
			logger.debug(" Populating the Metrics Context with Commands");

			ASBEntities asbEntities = AppContext.getAsbEntities();
			List<Namespace> namespaces = asbEntities.getNamespaces();
			List<Queue> queues = asbEntities.getQueues();
			List<Topic> topics = asbEntities.getTopics();
			List<Subscription> subscriptions = asbEntities.getSubscriptions();
			
			//System.out.println("Got the namespaces:"+namespaces.size()+", Queues:"+queues.size()+", Topics:"+topics.size()+", subscriptions:"+subscriptions.size());
			for(Queue queue: queues) {
				QueueCommand<QueueMetrics> qCmd = new QueueCommand<QueueMetrics>(queue);
				metricsContext.addMetricsTypeCommand(qCmd);
			}
			
			
			for(Topic topic: topics) {
				TopicCommand<TopicMetrics> topicCmd = new TopicCommand<TopicMetrics>(topic);
				metricsContext.addMetricsTypeCommand(topicCmd);
			}
		
			
			for(Namespace ns: namespaces) {
				NamespaceCommand<NamespaceMetrics> nsCmd = new NamespaceCommand<NamespaceMetrics>(ns);
				metricsContext.addMetricsTypeCommand(nsCmd);
			}
			
			for(Subscription sub: subscriptions) {
				SubscriptionCommand<SubscriptionMetrics> subCmd = new SubscriptionCommand<SubscriptionMetrics>(sub);
				metricsContext.addMetricsTypeCommand(subCmd);
			}
			logger.debug("Added Subscription "+subscriptions.size()+", Namespace "+namespaces.size()+", Topic "+topics.size()+", Queue "+queues.size()+" Commands to the Metrics Context");
			
			//Set the AAD OAuth2 AccessToken, since it will be used in NamespaceCommand for Monitor Metrics
			//RestTemplate rt = serviceConfig.getRestTemplateForAADAPI();
			//System.out.println("Defaut TenantId:"+AppContext.getDefaultTenantId()+", ClientId:"+AppContext.getClientId()+",ClientSecret:"+AppContext.getClientSecret());
			
			String oauth2AccessToken = serviceConfig.getOAuth2AccessToken(AppContext.getDefaultTenantId(), AppContext.getClientId(), AppContext.getClientSecret());
			
			logger.debug("====== Got the OAuth2 Access Token:\n"+StringUtils.isNotBlank(oauth2AccessToken));
			
			metricsContext.setAADAccessToken(oauth2AccessToken);
			//serviceConfig.releaseRestTemplateForAADAPI(rt);
		
		} catch(Exception e) {
			logger.error("Exception While Initializing the Metrics Context", e);
			throw new MetricsException("Exception While Initializing the Metrics Context", e);
		}
		
	}
	
	private void initializeConfigContext(ConfigContext configContext) {
		
		try {
			
			logger.debug("Populating the Config Context with Commands");
			
			ASBEntities asbEntities = AppContext.getAsbEntities();
			
			List<Namespace> namespaces = asbEntities.getNamespaces();
			List<Queue> queues = asbEntities.getQueues();
			List<Topic> topics = asbEntities.getTopics();
			List<Subscription> subscriptions = asbEntities.getSubscriptions();
			
			for(Queue queue: queues) {
				QueueCommand<QueueDescription> qCmd = new QueueCommand<QueueDescription>(queue);
				configContext.addConfigTypeCommand(qCmd, ConfigType.QUEUE);
			}
			
			for(Topic topic: topics) {
				TopicCommand<TopicDescription> topicCmd = new TopicCommand<TopicDescription>(topic);
				configContext.addConfigTypeCommand(topicCmd, ConfigType.TOPIC);
			}
			
			for(Namespace ns: namespaces) {
				NamespaceCommand<NamespaceDescription> nsCmd = new NamespaceCommand<NamespaceDescription>(ns);
				configContext.addConfigTypeCommand(nsCmd, ConfigType.NAMESPACE);
			}
			
			for(Subscription sub: subscriptions) {
				SubscriptionCommand<SubscriptionDescription> subCmd = new SubscriptionCommand<SubscriptionDescription>(sub);
				configContext.addConfigTypeCommand(subCmd, ConfigType.SUBSCRIPTION);
			}
			
			logger.debug("Added Queue "+queues.size()+", Topic "+topics.size()+", Subscription "+subscriptions.size()+
					", Namespace "+namespaces.size()+" Commands to the Config Context");
		
		} catch(Exception e) {
			logger.error("Got Exception while Populating Config Context with the Commands ", e);
			throw new ConfigException("Got Exception while Populating Config Context with the Commands ", e);
		}
		
		
	}
}
