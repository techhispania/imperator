package com.techhispania.imperator.server.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techhispania.imperator.common.annotations.Controller;
import com.techhispania.imperator.common.annotations.GetRequest;
import com.techhispania.imperator.common.annotations.PostRequest;
import com.techhispania.imperator.common.annotations.RequestBody;
import com.techhispania.imperator.core.http.dto.ImperatorResponse;
import com.techhispania.imperator.server.dto.DeployRequestDTO;
import com.techhispania.imperator.server.dto.DeployResponseDTO;

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
	
	@PostRequest("/deploy")
	public ImperatorResponse<DeployResponseDTO> deploy(@RequestBody DeployRequestDTO request) {
		logger.debug("Deploy service: {}", request);
		
		DeployResponseDTO response = new DeployResponseDTO("Service deployed");
		
		return new ImperatorResponse<DeployResponseDTO>(200, response);
	}	
}


