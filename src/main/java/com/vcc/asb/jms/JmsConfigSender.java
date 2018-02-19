package com.vcc.asb.jms;

import java.io.StringWriter;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JmsConfigSender extends AbstractJmsSender implements JmsSender {
	
	@Value("${jms.config.send.namespacename}")
	private String jmsConfigSendNamespaceName;
	
	@Value("${jms.config.send.keyname}")
	private String jmsConfigSendKeyName;
	
	@Value("${jms.config.send.keyvalue}")
	private String jmsConfigSendKeyValue;
	
	@Value("${jms.config.send.queuename}")
	private String jmsConfigSendQueueName;
	
	private static final String CONFIG_SEND_CONN_FACTORY_NAME = "CONFIG_SEND_CF";
	private static final String CONFIG_SEND_QUEUE_NAME = "CONFIG_SEND_QUEUE";
	
	public JmsConfigSender() {
		super(CONFIG_SEND_CONN_FACTORY_NAME, CONFIG_SEND_QUEUE_NAME);
	}
	
	@PostConstruct
	private void initialize() {
		
		try {
			logger.debug("Initialzing the JmsConfigSender Properties");
			String amqpsConnString = getAMQPSConnectionString(jmsConfigSendNamespaceName, jmsConfigSendKeyName, jmsConfigSendKeyValue);
			
			Properties props = new Properties();
			props.put("connectionfactory.CONFIG_SEND_CF", amqpsConnString);
			props.put("queue.CONFIG_SEND_QUEUE", jmsConfigSendQueueName);		
		
			logger.debug("Initializing the JmsConfigSender NamingContext");
			initNamingContext(props);
		
		} catch(Exception e) {
			
			logger.error("Exception Caught in JmsConfigSender while Initializing", e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean sendMessage(StringWriter msg) {
		
		boolean sendSuccess = false; 
		try {
			logger.debug("JmsConfigSender::Sending the message");
			sendSuccess = super.sendMessage(msg);
		
		} catch(Exception e) {
			logger.error("Exception while sending the JMS Message @ JmsConfigSender",e);
			throw new RuntimeException(e);
		}
		
		return sendSuccess;
	}	
	

}
