package com.techhispania.imperator.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techhispania.imperator.common.annotations.Controller;
import com.techhispania.imperator.common.annotations.GetRequest;

@Controller
public class ServerController {

	private static final Logger logger = LogManager.getLogger(ServerController.class);
	
	@GetRequest
	public String index() {
		logger.debug("Index Request");
		
		return "index";
	}
	
	@GetRequest("/health")
	public String health() {
		logger.debug("Health Request");
		
		return "health";
	}
}
