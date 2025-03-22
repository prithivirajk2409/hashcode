package com.hashcode.aws;

public abstract class BaseSQSMessage {
	private long queueInsertTime;
	private String tracerId;

	public long getQueueInsertTime() {
		return queueInsertTime;
	}

	public void setQueueInsertTime(long queueInsertTime) {
		this.queueInsertTime = queueInsertTime;
	}

	public String getTracerId() {
		return tracerId;
	}

	public void setTracerId(String tracerId) {
		this.tracerId = tracerId;
	}

	@Override
	public String toString() {
		return "BaseSQSMessage [queueInsertTime=" + queueInsertTime + ", tracerId=" + tracerId + "]";
	}

}
