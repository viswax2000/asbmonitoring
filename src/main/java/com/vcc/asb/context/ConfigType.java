package com.vcc.asb.context;

import com.vcc.asb.config.model.NamespaceDescription;
import com.vcc.asb.config.model.QueueDescription;
import com.vcc.asb.config.model.SubscriptionDescription;
import com.vcc.asb.config.model.TopicDescription;

public enum ConfigType {
	
	QUEUE(QueueDescription.class),
	TOPIC(TopicDescription.class),
	SUBSCRIPTION(SubscriptionDescription.class),
	NAMESPACE(NamespaceDescription.class);
	
	private Class clazz;
	
	private ConfigType(Class c) {
		this.clazz = c;
	}
	
	public Class getConfigTypeClass() {
		return this.clazz;
	}
	
	public static ConfigType getConfigType(Class cl) {
		ConfigType ct = null;
		for(ConfigType c: ConfigType.values()) {
			if(c.getConfigTypeClass().isAssignableFrom(cl)) {
				ct = c;
				break;
			}
		}
		return ct;
	}

}
