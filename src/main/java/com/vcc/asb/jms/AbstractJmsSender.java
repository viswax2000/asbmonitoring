package com.vcc.asb.jms;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Properties;
import java.util.UUID;

import javax.jms.BytesMessage;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.vcc.asb.context.BatchContext;
import com.vcc.asb.context.BatchType;

public abstract class AbstractJmsSender implements JmsSender {
	
	JmsTemplate jmsTemplate = null;
	Context context = null;
	String connFactoryName = null;
	String queueName = null;
	
	@Value("${sbgw.config.destination.property.name}")
	private String sbgwDestConfigPropertyName;
	
	@Value("${sbgw.config.destination.property.value}")
	private String sbgwDestConfigPropertyValue;
	
	@Value("${sbgw.metrics.destination.property.name}")
	private String sbgwDestMetricsPropertyName;

	@Value("${sbgw.metrics.destination.property.value}")
	private String sbgwDestMetricsPropertyValue;
	
	static Logger logger = null;
	
	AbstractJmsSender(String cfName, String qName) {
		this.connFactoryName = cfName;
		this.queueName = qName;
		logger = LoggerFactory.getLogger(this.getClass());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void initNamingContext(Properties props) throws NamingException {
		
		Hashtable ht = new Hashtable();
		ht.put(Context.INITIAL_CONTEXT_FACTORY, "com.vcc.asb.jms.PropertiesInitialContextFactory");
		ht.put(Context.PROVIDER_URL, props);
		
		this.context = new InitialContext(ht);
	}
	
	@Override
	public boolean sendMessage(StringWriter message) throws Exception {
		
		try {
			getJmsTemplate().send(new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					
					String msgId = UUID.randomUUID().toString();
					
					BytesMessage bMsg = session.createBytesMessage();
					bMsg.writeBytes(message.toString().getBytes());
					
					//TextMessage msg = session.createTextMessage(message.toString());
					//msg.setJMSMessageID("ID:"+msgId);
					
					if(BatchContext.getCurrentBatchType() == BatchType.METRICS) {
						//msg.setStringProperty(sbgwDestMetricsPropertyName, sbgwDestMetricsPropertyValue);
						bMsg.setStringProperty(sbgwDestMetricsPropertyName, sbgwDestMetricsPropertyValue);
						//logger.debug("Sending Metrics Msg:\n"+msg.getText());
					} else {
						//logger.debug("Sending Config Msg:\n"+msg.getText());
						//msg.setStringProperty(sbgwDestConfigPropertyName, sbgwDestConfigPropertyValue);
						bMsg.setStringProperty(sbgwDestConfigPropertyName, sbgwDestConfigPropertyValue);
					}
					
					//return msg;
					return bMsg;
				}
			});	
			
			return true;
			
		} catch(Exception e) {
			logger.error("Exception in AbstractJmsSender while sending the message", e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public JmsTemplate getJmsTemplate() throws Exception {
		
		JmsTemplate jmsTemplat = null;
		
		if(this.jmsTemplate == null) {
			ConnectionFactory connFactory = (ConnectionFactory)this.context.lookup(this.connFactoryName);
			Destination sendToQueue = (Destination)this.context.lookup(this.queueName);
			jmsTemplat = new JmsTemplate();
			jmsTemplat.setConnectionFactory(connFactory);
			jmsTemplat.setDefaultDestination(sendToQueue);
			
			this.jmsTemplate = jmsTemplat;
		} else {
			jmsTemplat = this.jmsTemplate;
		}
		
		return jmsTemplat;
	}	
	
	@Override
	public String getAMQPSConnectionString(String namespaceName, String metricsSendKeyName, String metricsSendKeyValue) {
		
		String encodedKeyValue = getEncodedString(metricsSendKeyValue);
		
		StringBuilder sb = new StringBuilder();
		sb.append("amqps://").append(metricsSendKeyName).append(":").append(encodedKeyValue)
							 .append("@").append(namespaceName)
							 .append(".servicebus.windows.net");
		
		return sb.toString();
	}
	

	
	protected String getEncodedString(String s) {
		
		String encodedString = null;
		try {
			encodedString = URLEncoder.encode(s, "UTF-8");
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return encodedString;
	}

}
