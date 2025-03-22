package com.hashcode.enums;

public enum DifficultyType {
	EASY(1), MEDIUM(2), HARD(3);

	int value;

	DifficultyType(int value) {
		this.value = value;
	}
}