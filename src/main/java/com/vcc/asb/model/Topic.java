package com.vcc.asb.model;

public class Topic extends BaseEntity {
	
	private String topicName;

	public Topic(String resGrpName, String nsName, String topicName) {
		super(resGrpName, nsName);
		this.topicName = topicName;
	}
	
	public String getTopicName() {
		return this.topicName;
	}

}
