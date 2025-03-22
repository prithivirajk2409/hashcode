package com.hashcode.entity;

public class ValidateTokenRequest {
	private String bearerToken;

	public String getBearerToken() {
		return bearerToken;
	}

	public void setBearerToken(String bearerToken) {
		this.bearerToken = bearerToken;
	}

	@Override
	public String toString() {
		return "ValidateTokenRequest [bearerToken=" + bearerToken + "]";
	}
}