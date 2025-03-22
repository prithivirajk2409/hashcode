package com.hashcode.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest {

	@JsonProperty("user_name")
	private String userName;

	@JsonProperty("password")
	private String password;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}