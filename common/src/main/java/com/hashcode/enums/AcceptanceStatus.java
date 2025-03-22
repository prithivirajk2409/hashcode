package com.hashcode.enums;

public enum AcceptanceStatus {
	ACCEPTED(1), WRONG_ANSWER(0), TIME_LIMIT_EXCEEDED(2), MEMORY_LIMIT_EXCEEDED(3), RUNTIME_ERROR(4), UNKNOWN_ERROR(5),
	COMPILATION_ERROR(-1);

	private int status;

	AcceptanceStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public static AcceptanceStatus getStatus(int status) {
		for (AcceptanceStatus acceptanceStatus : AcceptanceStatus.values()) {
			if (acceptanceStatus.getStatus() == status) {
				return acceptanceStatus;
			}
		}
		throw new IllegalArgumentException("Unexpected status value: " + status);
	}
}