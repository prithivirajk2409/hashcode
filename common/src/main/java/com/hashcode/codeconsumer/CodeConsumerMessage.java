package com.hashcode.codeconsumer;

import com.hashcode.aws.BaseSQSMessage;

public class CodeConsumerMessage extends BaseSQSMessage {

	private CodeConsumerTaskType taskType;
	private String data;

	public CodeConsumerMessage() {
		super();
	}

	public CodeConsumerMessage(String tracerId, CodeConsumerTaskType taskType, String data) {
		super();
		setTracerId(tracerId);
		this.data = data;
		this.taskType = taskType;
	}

	public CodeConsumerTaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(CodeConsumerTaskType taskType) {
		this.taskType = taskType;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "CodeConsumerMessage [taskType=" + taskType + ", data=" + data + "]";
	}

}