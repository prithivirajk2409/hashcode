package com.hashcode.codeconsumer;

public enum CodeConsumerTaskType {
	TEST(CodeConsumerVertical.EXECUTION), SUBMIT(CodeConsumerVertical.EXECUTION);

	private CodeConsumerVertical vertical;

	CodeConsumerTaskType(CodeConsumerVertical vertical) {
		this.vertical = vertical;
	}

	public CodeConsumerVertical getVertical() {
		return vertical;
	}

	public void setVertical(CodeConsumerVertical vertical) {
		this.vertical = vertical;
	}

}
