package com.vcc.asb.context;

import com.vcc.asb.metrics.command.BaseCommand;
import com.vcc.asb.metrics.command.NamespaceCommand;
import com.vcc.asb.metrics.command.QueueCommand;
import com.vcc.asb.metrics.command.SubscriptionCommand;
import com.vcc.asb.metrics.command.TopicCommand;

public enum MetricsCommandType {
	
	QUEUE_COMMAND(QueueCommand.class),
	TOPIC_COMMAND(TopicCommand.class),
	SUBSCRIPTION_COMMAND(SubscriptionCommand.class),
	NAMESPACE_COMMAND(NamespaceCommand.class);
	
	private Class<? extends BaseCommand> clazz;
	
	private MetricsCommandType(Class<? extends BaseCommand> clazz) {
		this.clazz = clazz;
	}
	
	public Class<? extends BaseCommand> getMetricsCommandClass() {
		return this.clazz;
	}
	
	public static MetricsCommandType getMetricsCommandType(Class clazz) {
		for(MetricsCommandType mct: MetricsCommandType.values()) {
			if(mct.getMetricsCommandClass().isAssignableFrom(clazz)) {
				return mct;
			}
		}
		
		return null;
	}

}
