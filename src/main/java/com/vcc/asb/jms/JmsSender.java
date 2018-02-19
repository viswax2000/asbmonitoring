package com.vcc.asb.jms;

import java.io.StringWriter;

import org.springframework.jms.core.JmsTemplate;

public interface JmsSender {

	JmsTemplate getJmsTemplate() throws Exception;

	String getAMQPSConnectionString(String namespaceName, String metricsSendKeyName, String metricsSendKeyValue);

	boolean sendMessage(StringWriter message) throws Exception;

}
