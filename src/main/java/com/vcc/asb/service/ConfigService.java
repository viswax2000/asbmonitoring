package com.vcc.asb.service;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vcc.asb.config.model.NamespaceDescription;
import com.vcc.asb.config.model.QueueDescription;
import com.vcc.asb.config.model.SubscriptionDescription;
import com.vcc.asb.config.model.TopicDescription;
import com.vcc.asb.configuration.ServiceConfig;
import com.vcc.asb.exception.ConfigException;
import com.vcc.asb.metrics.command.BaseCommand;
import com.vcc.asb.metrics.command.Command;
import com.vcc.asb.metrics.command.NamespaceCommand;
import com.vcc.asb.metrics.command.QueueCommand;
import com.vcc.asb.metrics.command.SubscriptionCommand;
import com.vcc.asb.metrics.command.TopicCommand;
import com.vcc.asb.util.ConfigFormatter;

@Service
public class ConfigService {

	@Autowired
	ServiceBusClient sbClient;
	
	@Autowired
	MgmtClient mgmtClient;
	
	@Autowired
	AzureServiceClient azureServiceClient;
	
	@Autowired
	ConfigFormatter configFormatter;
	
	@Autowired
	ServiceConfig serviceConfig;
	
	private Map<Class<? extends BaseCommand>, List<Client>> configClients;
	
	private static Logger logger = LoggerFactory.getLogger(ConfigService.class);
	
	public ConfigService() {
	}
	
	public void executeConfigCommands(List<Command> commands) {
		
		if(commands!=null && commands.size()>0) {
			logger.debug("ConfigService, Executing "+commands.size()+" "+commands.get(0).getEntityType().name()+" Config Commands");
		}
		
		for(Command command: commands) {
			
			inner:for(Client client: this.configClients.get(command.getClass())) {
				
				logger.debug("ConfigService::Executing the ConfigCommand:"+command.getClass()+", With Client:"+client.getClass());
				
				command.execute(client);
				logger.debug("Is Command Successful:"+command.isCommandSuccessful());

				if(command.isCommandSuccessful() == false) {
					continue inner;
				} else {
					break inner;
				}
			}
		}
		
		logger.debug("Building the Config Response for "+commands.size()+" Config Commands");
		
		try {
			
			List<StringWriter> response = buildConfigResponse(commands);
			logger.debug("Sending "+response.size()+" Config Responses");
			
			boolean sendSuccess = sendResponse(response);
			logger.debug("******* JMS Send Config Response Success:"+sendSuccess+" ************** ");
			
		} catch(Exception e) {
			logger.error("Caught Exception while executing the Config Commands", e);
			throw new ConfigException("Caught Exception while executing the Config Commands", e);
		}
	}
	
	public List<StringWriter> buildConfigResponse(List<Command> commands) {
		
		List<StringWriter> sw = new ArrayList<StringWriter>();
		
		List<NamespaceDescription> nsDescs = new ArrayList<NamespaceDescription>();
		List<QueueDescription> queueDescs = new ArrayList<QueueDescription>();
		List<TopicDescription> topicDescs = new ArrayList<TopicDescription>();
		List<SubscriptionDescription> subDescs = new ArrayList<SubscriptionDescription>();
		
		for(Command command: commands) {
			if(command.getClass() == NamespaceCommand.class) {
				nsDescs.add((NamespaceDescription)command.getCommandResult());
			} else if(command.getClass() == QueueCommand.class) {
				queueDescs.add((QueueDescription)command.getCommandResult());
			} else if(command.getClass() == TopicCommand.class) {
				topicDescs.add((TopicDescription)command.getCommandResult());
			} else if(command.getClass() == SubscriptionCommand.class) {
				subDescs.add((SubscriptionDescription)command.getCommandResult());
			}
		}
		
		int i = -1;
		if(nsDescs.size()>0) {
			logger.debug("ConfigResponse::Number of NamespaceDescriptions:"+nsDescs.size());
			sw.add(new StringWriter());
			i = i + 1;
			sw.get(i).write(this.configFormatter.processNamespaceDescriptions(nsDescs));
		}
		
		if(queueDescs.size()>0) {
			logger.debug("ConfigResponse::Number of QueueDescriptions:"+queueDescs.size());
			sw.add(new StringWriter());
			i = i + 1;
			sw.get(i).write(this.configFormatter.processQueueDescriptions(queueDescs));
		}
		
		if(topicDescs.size()>0) {
			logger.debug("ConfigResponse::Number of TopicDescriptions:"+topicDescs.size());
			sw.add(new StringWriter());
			i = i + 1;
			sw.get(i).write(this.configFormatter.processTopicDescriptions(topicDescs));
		}
		
		if(subDescs.size()>0) {
			logger.debug("ConfigResponse::Number of SubscriptionDescriptions:"+subDescs.size());
			sw.add(new StringWriter());
			i = i + 1;
			sw.get(i).write(this.configFormatter.processSubscriptionDescriptions(subDescs));
		}
		
		return sw;
	}
	
	
	public boolean sendResponse(List<StringWriter> responses) throws Exception {

		boolean responseSendSuccess = false;
		
		try {
			logger.debug("ConfigResponse::Total Number of Responses:"+responses.size());
			
			for(StringWriter sw: responses) {
				responseSendSuccess = this.serviceConfig.getJmsConfigSender().sendMessage(sw);
			}
			responseSendSuccess = true;
			
		} catch(Exception e) {
			responseSendSuccess = false;
			logger.error("Caught Exception While sending Config Response to JMS", e);
			throw new ConfigException("Caught Exception While sending Config Response to JMS", e);
		}
		
		return responseSendSuccess;
	}
	
	@PostConstruct
	private void initConfigClients() {
		logger.debug("==================== Initializing ConfigClients ==================");
		this.configClients = new HashMap<Class<? extends BaseCommand>,List<Client>>();
		this.configClients.put(QueueCommand.class, Arrays.asList(sbClient, mgmtClient));
		this.configClients.put(TopicCommand.class, Arrays.asList(sbClient, mgmtClient));
		this.configClients.put(SubscriptionCommand.class, Arrays.asList(sbClient, mgmtClient));
		this.configClients.put(NamespaceCommand.class, Arrays.asList(azureServiceClient, mgmtClient));
		logger.debug("==================== Initialized ConfigClients ==================");
		
	}
	
}
