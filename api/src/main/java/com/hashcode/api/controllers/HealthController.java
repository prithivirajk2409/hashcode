package com.hashcode.api.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.hashcode.api.response.SystemHealthResponse;
import com.hashcode.api.service.HealthService;

@RestController
@RequestMapping("/internal/health")
public class HealthController {

	private static final Logger logger = LogManager.getLogger(HealthController.class);

	@Autowired
	private HealthService healthService;

	@GetMapping(value = "/check", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public SystemHealthResponse getHealthCheck() throws Exception {
		logger.info("GET - Received Healthcheck at : {}", System.currentTimeMillis());
		return healthService.getHealthCheck();
	}

}