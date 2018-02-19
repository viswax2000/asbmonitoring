package com.vcc.asb.model;

public class Subscription extends BaseEntity {

	private String topicName;
	private String subName;
	
	protected Subscription(String resGrpName, String nsName, String topicName, String subName) {
		super(resGrpName, nsName);
		this.topicName = topicName;
		this.subName = subName;
	}
	
	public String getTopicName() {
		return this.topicName;
	}
	
	public String getSubscriptionName() {
		return this.subName;
	}

}
