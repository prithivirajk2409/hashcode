package com.hashcode.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HCTopicTagMaster {

	@JsonProperty("topic_id")
	private int topicId;

	@JsonProperty("topic_name")
	private String topicName;

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

}