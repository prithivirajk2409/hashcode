package com.hashcode.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SignUpRequest {

	@JsonProperty("user_name")
	private String userName;

	@JsonProperty("password")
	private String password;

	@JsonProperty("email_id")
	private String emailId;

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

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	@Override
	public String toString() {
		return "SignUpRequest [userName=" + userName + ", password=" + password + ", emailId=" + emailId + "]";
	}

}