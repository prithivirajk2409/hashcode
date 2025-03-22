package com.hashcode.enums;

public enum DBUserType {
	USER("rw-user");

	private final String userType;

	DBUserType(String userType) {
		this.userType = userType;
	}

	public String getUserType() {
		return userType;
	}

}