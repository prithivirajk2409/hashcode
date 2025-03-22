package com.hashcode.enums;

public enum ProgrammingLanguage {
	CPP(1), JAVA(2), PYTHON(3);

	private int type;

	ProgrammingLanguage(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
