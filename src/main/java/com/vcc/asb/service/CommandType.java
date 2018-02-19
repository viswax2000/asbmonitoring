package com.vcc.asb.service;

import com.vcc.asb.metrics.command.NamespaceCommand;
import com.vcc.asb.metrics.command.QueueCommand;
import com.vcc.asb.metrics.command.SubscriptionCommand;
import com.vcc.asb.metrics.command.TopicCommand;

public enum CommandType {
	
	QUEUE(QueueCommand.class),
	TOPIC(TopicCommand.class),
	NAMESPACE(NamespaceCommand.class),
	SUBSCRIPTION(SubscriptionCommand.class);
	
	private Class clazz;
	
	private CommandType(Class c) {
		this.clazz = c;
	}
	
	public Class getCommandTypeClass() {
		return this.clazz;
	}
	
	public CommandType getCommandType(Class c) {
		CommandType cType = null;
		for(CommandType ct: CommandType.values()) {
			if(ct.getClass().isAssignableFrom(c)) {
				cType = ct;
				break;
			}
		}
		return cType;
	}

}
