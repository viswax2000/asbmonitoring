package com.vcc.asb.context;

import com.vcc.asb.metrics.model.NamespaceMetrics;
import com.vcc.asb.metrics.model.QueueMetrics;
import com.vcc.asb.metrics.model.SubscriptionMetrics;
import com.vcc.asb.metrics.model.TopicMetrics;

public enum MetricsEntityType {
	
	QUEUE(QueueMetrics.class),
	TOPIC(TopicMetrics.class),
	SUBSCRIPTION(SubscriptionMetrics.class),
	NAMESPACE(NamespaceMetrics.class);
	
	private Class<?> clazz;
	private MetricsEntityType(Class<?> c) {
		this.clazz = c;
	}
	
	public Class<?> getMetricsClass() {
		return this.clazz;
	}
	
	public static MetricsEntityType getMetricsEntityType(Class<?> c) {
		MetricsEntityType t = null;
		for(MetricsEntityType mt: MetricsEntityType.values()) {
			if(mt.getMetricsClass().isAssignableFrom(c)) {
				t = mt;
			}
		}
		return t;
	}

}
