package com.hashcode.enums;

public enum AppMode {
	DEVELOPMENT("dev"), PRODUCTION("prod");

	private final String shortName;

	AppMode(String name) {
		this.shortName = name;
	}

	public String getShortName() {
		return shortName;
	}

}