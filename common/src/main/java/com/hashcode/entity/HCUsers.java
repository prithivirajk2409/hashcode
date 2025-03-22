package com.hashcode.entity;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HCUsers {

	@JsonProperty("user_id")
	private int userId;

	@JsonProperty("user_name")
	private String userName;

	@JsonProperty("password")
	@JsonIgnore
	private String password;

	@JsonProperty("email_id")
	private String emailId;

	private Timestamp created;
	private Timestamp updated;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

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

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public Timestamp getUpdated() {
		return updated;
	}

	public void setUpdated(Timestamp updated) {
		this.updated = updated;
	}

	@Override
	public String toString() {
		return "HCUsers [userId=" + userId + ", userName=" + userName + ", password=" + password + ", emailId="
				+ emailId + ", created=" + created + ", updated=" + updated + "]";
	}

}
