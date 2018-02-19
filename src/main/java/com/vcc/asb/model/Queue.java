package com.vcc.asb.model;

public class Queue extends BaseEntity {

	private String queueName;
	
	protected Queue(String resGrpName, String nsName, String queueName) {
		super(resGrpName, nsName);
		this.queueName = queueName;
	}
	
	public String getQueueName() {
		return this.queueName;
	}

}
