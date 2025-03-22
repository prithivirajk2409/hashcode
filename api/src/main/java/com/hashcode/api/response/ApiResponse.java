package com.hashcode.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ApiResponse<T> {

	private boolean response;
	private String message;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T data;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public boolean isResponse() {
		return response;
	}

	public void setResponse(boolean response) {
		this.response = response;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}