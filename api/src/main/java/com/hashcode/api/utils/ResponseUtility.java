package com.hashcode.api.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.hashcode.api.response.ApiResponse;

public class ResponseUtility {

	private static final String KEY_FAILURE = "failure";
	private static final String KEY_SUCCESS = "success";

	// Success Responses
	public static <T> ResponseEntity<ApiResponse<T>> successResponse(ApiResponse<T> response) {
		return new ResponseEntity<>(response, getHeader(), HttpStatus.OK);
	}

	public static <T> ResponseEntity<ApiResponse<T>> successResponse(T body) {
		ApiResponse<T> response = new ApiResponse<>();
		response.setResponse(true);
		response.setData(body);
		response.setMessage(KEY_SUCCESS);
		return new ResponseEntity<>(response, getHeader(), HttpStatus.OK);
	}

	public static <T> ResponseEntity<ApiResponse<T>> successResponse(String msg) {
		ApiResponse<T> response = new ApiResponse<>();
		response.setResponse(true);
		response.setMessage(msg);
		return new ResponseEntity<>(response, getHeader(), HttpStatus.OK);
	}

	public static <T> ResponseEntity<ApiResponse<T>> successResponse(String msg, HttpStatus status) {
		ApiResponse<T> response = new ApiResponse<>();
		response.setResponse(true);
		response.setMessage(msg);
		return new ResponseEntity<>(response, getHeader(), status);
	}

	public static <T> ResponseEntity<ApiResponse<T>> successResponse(T body, HttpStatus status) {
		ApiResponse<T> response = new ApiResponse<>();
		response.setResponse(true);
		response.setData(body);
		response.setMessage(KEY_SUCCESS);
		return new ResponseEntity<>(response, getHeader(), status);
	}

	public static <T> ResponseEntity<ApiResponse<T>> successResponse(T body, String msg) {
		ApiResponse<T> response = new ApiResponse<>();
		response.setResponse(true);
		response.setData(body);
		response.setMessage(msg);
		return new ResponseEntity<>(response, getHeader(), HttpStatus.OK);
	}

	public static <T> ResponseEntity<ApiResponse<T>> successResponse(T body, HttpStatus status, String msg) {
		ApiResponse<T> response = new ApiResponse<>();
		response.setResponse(true);
		response.setData(body);
		response.setMessage(msg);
		return new ResponseEntity<>(response, getHeader(), status);
	}

	// Failure Responses
	public static <T> ResponseEntity<ApiResponse<T>> errorResponse(ApiResponse<T> response) {
		return new ResponseEntity<>(response, getHeader(), HttpStatus.BAD_REQUEST);
	}

	public static <T> ResponseEntity<ApiResponse<T>> errorResponse() {
		ApiResponse<T> response = new ApiResponse<>();
		response.setResponse(false);
		response.setMessage(KEY_FAILURE);
		return new ResponseEntity<>(response, getHeader(), HttpStatus.BAD_REQUEST);
	}

	public static <T> ResponseEntity<ApiResponse<T>> errorResponse(HttpStatus status) {
		ApiResponse<T> response = new ApiResponse<>();
		response.setResponse(false);
		response.setMessage(KEY_FAILURE);
		return new ResponseEntity<>(response, getHeader(), status);
	}

	public static <T> ResponseEntity<ApiResponse<T>> errorResponse(String msg) {
		ApiResponse<T> response = new ApiResponse<>();
		response.setResponse(false);
		response.setMessage(msg);
		return new ResponseEntity<>(response, getHeader(), HttpStatus.BAD_REQUEST);
	}

	public static <T> ResponseEntity<ApiResponse<T>> errorResponse(HttpStatus status, String msg) {
		ApiResponse<T> response = new ApiResponse<>();
		response.setResponse(false);
		response.setMessage(msg);
		return new ResponseEntity<>(response, getHeader(), status);
	}

	// Header loader
	public static HttpHeaders getHeader() {
		HttpHeaders header = new HttpHeaders();
		return header;
	}
}