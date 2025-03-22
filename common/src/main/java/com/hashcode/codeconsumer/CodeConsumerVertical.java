package com.hashcode.codeconsumer;

public enum CodeConsumerVertical {
	EXECUTION("code-execution-queue");

	String queueName;

	CodeConsumerVertical(String queueName) {
		this.queueName = queueName;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

}