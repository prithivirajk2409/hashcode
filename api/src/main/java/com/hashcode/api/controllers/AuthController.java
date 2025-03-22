package com.hashcode.api.controllers;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hashcode.entity.LoginRequest;
import com.hashcode.entity.SignUpRequest;
import com.hashcode.entity.ValidateTokenRequest;
import com.hashcode.api.response.ApiResponse;
import com.hashcode.api.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private static final Logger logger = LogManager.getLogger(AuthController.class);

	@Autowired
	private AuthService authService;

	@PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<ApiResponse<Map<String, Object>>> signUpUser(@RequestBody SignUpRequest body) {
		logger.info("Signup User Request  at time : {}", System.currentTimeMillis());
		return authService.processSignUpUserRequest(body);
	}

	@PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<ApiResponse<Map<String, Object>>> loginUser(@RequestBody LoginRequest body) {
		logger.info("Login Request  at time : {}", System.currentTimeMillis());
		return authService.processLoginUserRequest(body);
	}
	
	
	@PostMapping(value = "/refresh/token", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<ApiResponse<Map<String, Object>>> refreshToken(@RequestBody ValidateTokenRequest body) {
		logger.info("Refresh token  Request  at time : {}", System.currentTimeMillis());
		return authService.processRefreshTokenRequest(body);
	}

}