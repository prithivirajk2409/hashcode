package com.hashcode.enums;

public enum CodeExecutionJobStatus {
	PENDING(0), STARTED(2), COMPLETED(1), ERROR(-1);

	int status;

	CodeExecutionJobStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public static CodeExecutionJobStatus getCodeExecutionJobStatus(int status) {
		for (CodeExecutionJobStatus codeExecutionJobStatus : CodeExecutionJobStatus.values()) {
			if (codeExecutionJobStatus.getStatus() == status) {
				return codeExecutionJobStatus;
			}
		}
		throw new IllegalArgumentException("Unexpected status value: " + status);
	}

}