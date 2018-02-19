package com.vcc.asb.service;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vcc.asb.configuration.ServiceConfig;
import com.vcc.asb.context.AppContext;
import com.vcc.asb.context.MetricsCommandType;
import com.vcc.asb.exception.MetricsException;
import com.vcc.asb.metrics.command.BaseCommand;
import com.vcc.asb.metrics.command.Command;
import com.vcc.asb.metrics.command.MetricsType;
import com.vcc.asb.metrics.command.NamespaceCommand;
import com.vcc.asb.metrics.command.QueueCommand;
import com.vcc.asb.metrics.command.SubscriptionCommand;
import com.vcc.asb.metrics.command.TopicCommand;
import com.vcc.asb.metrics.model.NamespaceMetrics;
import com.vcc.asb.metrics.model.QueueMetrics;
import com.vcc.asb.metrics.model.SubscriptionMetrics;
import com.vcc.asb.metrics.model.TopicMetrics;
import com.vcc.asb.model.Namespace;
import com.vcc.asb.model.Queue;
import com.vcc.asb.model.Subscription;
import com.vcc.asb.model.Topic;

@Service
public class MetricsService {
	
	private Map<Class<? extends BaseCommand>, Map<MetricsType, Client>> metricsClients;
	
	@Autowired
	MonitorClient monitorClient;
	
	@Autowired
	MgmtClient mgmtClient;
	
	@Autowired
	ServiceBusClient sbClient;
	
	@Autowired
	ServiceConfig serviceConfig;
	
	private static Logger logger = LoggerFactory.getLogger(MetricsService.class);
	
	public MetricsService() {
		
	}
	
	@PostConstruct
	public void init() {
		initMetricsClients();		
	}
	
	public void executeMetricsCommands(List<Command> commands) {

		try {
		
			MetricsCommandType commandType = null;
			
			for(Command command: commands) {
				
				HashMap<MetricsType, Client> clients = (HashMap<MetricsType, Client>) this.metricsClients.get(command.getClass());
				
				if(commandType==null) {
					commandType = MetricsCommandType.getMetricsCommandType(command.getClass());
				}
	
				for(Entry<MetricsType, Client> entry: clients.entrySet()) {
					
					logger.debug("******* Executing the MetricsCommand: "+command.getClass().getSimpleName()+", MetricType:"+entry.getKey()+
							", NamespaceName:"+command.getEntity().getNamespaceName()+", with Client:"+entry.getValue().getClass().getSimpleName());
					
					command.setMetricsType(entry.getKey());
					command.execute(entry.getValue());
					
					logger.debug("******  Executed the MetricsCommand ***  ");
					
				}
			}
		
			List<StringWriter> metricsResponse = buildMetricsResponse(commands);
			boolean responseSendSuccess = sendMetricsResponse(metricsResponse, commandType);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<StringWriter> buildMetricsResponse(List<Command> commands) {

		List<StringWriter> sw = new ArrayList<StringWriter>();
		
		List<QueueMetrics> queueMetrics = new ArrayList<QueueMetrics>();
		List<TopicMetrics> topicMetrics = new ArrayList<TopicMetrics>();
		List<SubscriptionMetrics> subMetrics = new ArrayList<SubscriptionMetrics>();
		List<NamespaceMetrics> nsMetrics = new ArrayList<NamespaceMetrics>();

		logger.debug("Building the Metrics Response");
		
		for(Command command: commands) {
			logger.debug("Metrics Resposne for Command "+command.getClass().getSimpleName()+", isSuccessful:"+command.isCommandSuccessful()+
										",(command.getCommandResult()==null:"+(command.getCommandResult()==null));
			
			if(command.getClass() == QueueCommand.class) {
				if((command.isCommandSuccessful()) && (command.getCommandResult()!=null) && (command.getCommandResult() instanceof QueueMetrics)) {
					queueMetrics.add((QueueMetrics)command.getCommandResult());
					Queue q = ((QueueCommand)command).getEntity();
					logger.debug("Queue Metrics Successful for Queue:"+q.getQueueName()+", NamespaceName:"+q.getNamespaceName());
				} else {
					QueueMetrics qm = new QueueMetrics();
					qm.setResourceGroupName(command.getEntity().getResourceGroupName());
					qm.setNamespaceName(command.getEntity().getNamespaceName());
					qm.setQueueName(((Queue)command.getEntity()).getQueueName());
					qm.setFailureMessage(command.getCommandException().getMessage());
					queueMetrics.add(qm);
					logger.debug("Queue Metrics not successful for Queue:"+qm.getQueueName()+", NamespaceName:"+qm.getNamespaceName()+",FailureMsg:"+qm.getFailureMessage());
				}
			} else if(command.getClass() == TopicCommand.class) {
				if((command.isCommandSuccessful()) && (command.getCommandResult()!=null) && (command.getCommandResult() instanceof TopicMetrics)) {
					topicMetrics.add((TopicMetrics)command.getCommandResult());
					Topic t = ((TopicCommand)command).getEntity();
					logger.debug("Topic Metrics Successful for Topic:"+t.getTopicName()+", NamespaceName:"+t.getNamespaceName());
				} else {
					TopicMetrics tm = new TopicMetrics();
					tm.setResourceGroupName(command.getEntity().getResourceGroupName());
					tm.setTopicName(((Topic)command.getEntity()).getTopicName());
					tm.setNamespaceName(command.getEntity().getNamespaceName());
					tm.setFailureMessage(command.getCommandException().getMessage());
					topicMetrics.add(tm);
					logger.debug("Topic Metrics not successful for Topic:"+tm.getTopicName()+", NamespaceName:"+tm.getNamespaceName()+",FailureMsg:"+tm.getFailureMessage());
				}
			} else if(command.getClass() == SubscriptionCommand.class) {
				if((command.isCommandSuccessful()) && (command.getCommandResult()!=null) && (command.getCommandResult() instanceof SubscriptionMetrics)) {
					subMetrics.add((SubscriptionMetrics)command.getCommandResult());
					Subscription s = ((SubscriptionCommand)command).getEntity();
					logger.debug("Subscription Metrics Successful for Subscription:"+s.getSubscriptionName()+", TopicName:"+s.getTopicName()+", NamespaceName:"+s.getNamespaceName());
				} else {
					SubscriptionMetrics sm = new SubscriptionMetrics();
					sm.setResourceGroupName(command.getEntity().getResourceGroupName());
					sm.setNamespaceName(command.getEntity().getNamespaceName());
					sm.setSubscriptionName(((Subscription)command.getEntity()).getSubscriptionName());
					sm.setTopicName(((Subscription)command.getEntity()).getTopicName());
					sm.setFailureMessage(command.getCommandException().getMessage());
					subMetrics.add(sm);
					logger.debug("Subscription Metrics not Successful for Subscription:"+sm.getSubscriptionName()+",TopicName:"+sm.getTopicName()+
							",NamespaceName:"+sm.getNamespaceName()+",FailureMsg:"+sm.getFailureMessage());
				}
			} else if(command.getClass() == NamespaceCommand.class) {
				if((command.isCommandSuccessful()) && (command.getCommandResult()!=null) && (command.getCommandResult() instanceof NamespaceMetrics)) {
					nsMetrics.add((NamespaceMetrics)command.getCommandResult());
					Namespace n = ((NamespaceCommand)command).getEntity();
					logger.debug("Namespace Metrics Successful for Namespace:"+n.getNamespaceName()+", under ResourceGroup:"+n.getResourceGroupName());
				} else {
					NamespaceMetrics nm = new NamespaceMetrics();
					nm.setResourceGroupName(command.getEntity().getResourceGroupName());
					nm.setNamespaceName(command.getEntity().getNamespaceName());
					nm.setFailureMessage(command.getCommandException().getMessage());
					nsMetrics.add(nm);
					logger.debug("Namespace Metrics not Successful for Namespace:"+nm.getNamespaceName()+", ResourceGroup:"+nm.getResourceGroupName()+
							", Failure Msg:"+nm.getFailureMessage());
				}
			}
		}
		
		try {

			JAXBContext j = JAXBContext.newInstance(QueueMetrics.class, TopicMetrics.class, SubscriptionMetrics.class, NamespaceMetrics.class);
			Marshaller m = j.createMarshaller();
			m.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
			//m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "");
			//m.setProperty(Marshaller.JAXB_FRAGMENT, true);

			if(queueMetrics.size()>0) {
				logger.debug("Marshalling "+queueMetrics.size()+" Queue Metrics");
				sw.addAll(marshallQueueMetrics(queueMetrics, m));
			}
			
			if(topicMetrics.size()>0) {
				logger.debug("Marshalling "+topicMetrics.size()+" Topic Metrics");
				sw.addAll(marshallTopicMetrics(topicMetrics, m));
			}
			
			if(subMetrics.size()>0) {
				logger.debug("Marshalling "+subMetrics.size()+" Subscription Metrics");
				sw.addAll(marshallSubscriptionMetrics(subMetrics, m));
			}
			
			if(nsMetrics.size()>0) {
				logger.debug("Marshalling "+nsMetrics.size()+" Namespace Metrics");
				sw.addAll(marshallNamespaceMetrics(nsMetrics, m));
			}
		
		} catch(JAXBException e) {
			logger.error("Caught JAXBException While Marshalling", e);
			throw new MetricsException("Caught JAXBException While Marshalling", e);
		}
		
		return sw;
		
	}
	
	private List<StringWriter> marshallQueueMetrics(List<QueueMetrics> queueMetrics, Marshaller m) throws JAXBException {
		
		int i = 0;
			
		List<StringWriter> qmWriter = new ArrayList<StringWriter>();
		qmWriter.add(new StringWriter());
		qmWriter.get(i).write("<AzureSBQueueStatistics>");
		
		for(QueueMetrics qMetric: queueMetrics) {

			if(qmWriter.get(i).toString().length() > AppContext.getMessageSizeInBytes()) {
				
				qmWriter.get(i).write("</AzureSBQueueStatistics>");
				qmWriter.add(new StringWriter());
				i = i + 1;
				qmWriter.get(i).write("<AzureSBQueueStatistics>");
			} 
			m.marshal(qMetric, qmWriter.get(i));
		}
		
		qmWriter.get(i).write("</AzureSBQueueStatistics>");
		return qmWriter;
		
	}
	
	private List<StringWriter> marshallTopicMetrics(List<TopicMetrics> topicMetrics, Marshaller m) throws JAXBException {
		
		int i = 0;
		
		List<StringWriter> tmWriter = new ArrayList<StringWriter>();
		tmWriter.add(new StringWriter());
		tmWriter.get(i).write("<AzureSBTopicStatistics>");
		
		for(TopicMetrics topicMetric: topicMetrics) {
			
			if(tmWriter.get(i).toString().length() > AppContext.getMessageSizeInBytes()) {
				tmWriter.get(i).write("</AzureSBTopicStatistics>");
				tmWriter.add(new StringWriter());
				i = i + 1;
				tmWriter.get(i).write("<AzureSBTopicStatistics>");
			} 
			m.marshal(topicMetric, tmWriter.get(i));
		}
		
		tmWriter.get(i).write("</AzureSBTopicStatistics>");
		
		return tmWriter;
		
	}
	
	private List<StringWriter> marshallSubscriptionMetrics(List<SubscriptionMetrics> subMetrics, Marshaller m) throws JAXBException {
		
		int i = 0;
		
		List<StringWriter> smWriter = new ArrayList<StringWriter>();
		smWriter.add(new StringWriter());
		smWriter.get(i).write("<AzureSBSubscriptionStatistics>");
		
		for(SubscriptionMetrics subMetric: subMetrics) {
			
			if(smWriter.get(i).toString().length() > AppContext.getMessageSizeInBytes()) {
				smWriter.get(i).write("</AzureSBSubscriptionStatistics>");
				smWriter.add(new StringWriter());
				i = i + 1;
				smWriter.get(i).write("<AzureSBSubscriptionStatistics>");
			} 
			m.marshal(subMetric, smWriter.get(i));
		}

		smWriter.get(i).write("</AzureSBSubscriptionStatistics>");
		
		return smWriter;
	
	}
	
	private List<StringWriter> marshallNamespaceMetrics(List<NamespaceMetrics> nsMetrics, Marshaller m) throws JAXBException {
		
		int i = 0;
		
		List<StringWriter> nmWriter = new ArrayList<StringWriter>();
		nmWriter.add(new StringWriter());
		nmWriter.get(i).write("<AzureSBNamespaceStatistics>");
		
		for(NamespaceMetrics nsMetric: nsMetrics) {
			
			if(nmWriter.get(i).toString().length() > AppContext.getMessageSizeInBytes()) {
				nmWriter.get(i).write("</AzureSBNamespaceStatistics>");
				nmWriter.add(new StringWriter());
				i = i + 1;
				nmWriter.get(i).write("<AzureSBNamespaceStatistics>");
			} 
			m.marshal(nsMetric, nmWriter.get(i));
		}
		nmWriter.get(i).write("</AzureSBNamespaceStatistics>");			
		
		
		return nmWriter;
	}
	
	public boolean sendMetricsResponse(List<StringWriter> sWriters, MetricsCommandType metricsCmdType) {
		
		boolean sendSuccess = false;
		
		try {
			
			if(metricsCmdType!=null) {
				logger.debug("Sending the Metrics Response for CommandType:"+metricsCmdType.getMetricsCommandClass().getSimpleName());
			}
			
			for(StringWriter sw: sWriters) {
				this.serviceConfig.getJmsMetricsSender().sendMessage(sw);
			}
			sendSuccess = true;
		
		} catch(Exception e) {
			sendSuccess = false;
			logger.error("Got Exception while sending Metrics Message", e);
			throw new MetricsException("Got Exception while sending Metrics Msg", e);
		}
		return sendSuccess;
	}
	
	public void initMetricsClients() {
		
		this.metricsClients = new HashMap<Class<? extends BaseCommand>, Map<MetricsType, Client>>();
		
		this.metricsClients.put(QueueCommand.class, new HashMap<MetricsType, Client>() {{
				put(MetricsType.ROLLUP_METRICS, mgmtClient);
				put(MetricsType.CONFIG_METRICS, sbClient);
		}});
		
		this.metricsClients.put(TopicCommand.class, new HashMap<MetricsType, Client>() {{
				put(MetricsType.ROLLUP_METRICS, mgmtClient);
				put(MetricsType.CONFIG_METRICS, sbClient);
		}});
		
		this.metricsClients.put(SubscriptionCommand.class, new HashMap<MetricsType, Client>() {{
				put(MetricsType.ROLLUP_METRICS, mgmtClient);
				put(MetricsType.CONFIG_METRICS, sbClient);
		}});
		
		this.metricsClients.put(NamespaceCommand.class, new HashMap<MetricsType, Client>() {{
				put(MetricsType.NAMESPACE_METRICS, monitorClient);
		}});		
	}

}
