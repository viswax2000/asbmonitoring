package com.vcc.asb.jms;

import java.io.StringWriter;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JmsMetricsSender extends AbstractJmsSender implements JmsSender {
	
	@Value("${jms.metrics.send.namespacename}")
	private String jmsMetricsSendNamespaceName;
	
	@Value("${jms.metrics.send.keyname}")
	private String jmsMetricsSendKeyName;
	
	@Value("${jms.metrics.send.keyvalue}")
	private String jmsMetricsSendKeyValue;
	
	@Value("${jms.metrics.send.queuename}")
	private String jmsMetricsSendQueueName;
	
	private static final String METRICS_SEND_CONN_FACTORY_NAME = "METRICS_SEND_CF";
	private static final String METRICS_SEND_QUEUE_NAME = "METRICS_SEND_QUEUE";
	
	
	public JmsMetricsSender() {
		super(METRICS_SEND_CONN_FACTORY_NAME, METRICS_SEND_QUEUE_NAME);
	}
	
	@PostConstruct
	private void initialize() {
		
		try {
			logger.debug("Initialzing the JmsMetricsSender Properties");
			String amqpsConnString = getAMQPSConnectionString(jmsMetricsSendNamespaceName, jmsMetricsSendKeyName, jmsMetricsSendKeyValue);
			
			Properties props = new Properties();
			props.put("connectionfactory.METRICS_SEND_CF", amqpsConnString);
			props.put("queue.METRICS_SEND_QUEUE", jmsMetricsSendQueueName);		
		
			logger.debug("Initializing the JmsMetricsSender NamingContext");
			initNamingContext(props);
		
		} catch(Exception e) {
			
			logger.error("Exception Caught in JmsMetricsSender while Initializing", e);
			throw new RuntimeException(e);

		}
	}
	
	@Override
	public boolean sendMessage(StringWriter msg) {
		
		boolean sendSuccess = false; 
		try {
			logger.debug("Sending Metrics Message @ JmsMetricsSender");
			sendSuccess = super.sendMessage(msg);
		
		} catch(Exception e) {
			logger.error("Caught Exception @ JmsMetricsSender while sending the Message", e);
			throw new RuntimeException(e);
		}
		
		return sendSuccess;
	}
	
}
