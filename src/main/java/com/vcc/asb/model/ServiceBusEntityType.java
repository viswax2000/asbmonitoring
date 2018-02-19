package com.vcc.asb.model;

public enum ServiceBusEntityType {
	
	NAMESPACE("Namespace"),
	QUEUE("Queue"),
	TOPIC("Topic"),
	SUBSCRIPTION("Subscription");
	
	private String entityType;
	
	private ServiceBusEntityType(String entityType) {
		this.entityType = entityType;
	}
	
	public String getEntityType() {
		return this.entityType;
	}

}
