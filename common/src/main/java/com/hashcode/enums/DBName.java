package com.hashcode.enums;

public enum DBName {

	PRIMARY("default");

	private final String name;

	DBName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}