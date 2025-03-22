package com.hashcode.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HCProblemTopicTagMapper {

	@JsonProperty("problem_id")
	private int problemId;

	@JsonProperty("topic_id")
	private int topicId;

	public int getProblemId() {
		return problemId;
	}

	public void setProblemId(int problemId) {
		this.problemId = problemId;
	}

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	@Override
	public String toString() {
		return "HCProblemTopicTagMapper [problemId=" + problemId + ", topicId=" + topicId + "]";
	}

}