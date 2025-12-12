package com.techhispania.imperator.server.controllers;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techhispania.imperator.common.annotations.Controller;
import com.techhispania.imperator.common.annotations.GetRequest;
import com.techhispania.imperator.common.annotations.PostRequest;
import com.techhispania.imperator.common.annotations.RequestBody;
import com.techhispania.imperator.common.exceptions.ImperatorException;
import com.techhispania.imperator.server.dto.DeployRequestDTO;
import com.techhispania.imperator.server.dto.mappers.ApplicationMapper;
import com.techhispania.imperator.server.services.ApplicationService;
import com.techhispania.imperator.server.services.DeployService;
import com.techhispania.imperator.server.services.factories.ApplicationServiceFactory;
import com.techhispania.imperator.server.services.factories.DeployServiceFactory;

@Controller
public class ServerController {

	private static final Logger logger = LogManager.getLogger(ServerController.class);
	
	private final DeployService deployService = DeployServiceFactory.createDeployServiceImpl();
	private final ApplicationService applicationService = ApplicationServiceFactory.createApplicationServiceImpl();
	
	@GetRequest
	public String index(Map<String, Object> model) {
		logger.debug("Index Request");
		
		model.put("appName", "Imperator");
		
		try {
			model.put("applications", applicationService.getAllDeployedApplications());
		} catch (ImperatorException e) {
			logger.error("Error processing index request.", e);
			return "error";
		}
		
		return "index";
	}
	
	@GetRequest("/health")
	public String health() {
		logger.debug("Health Request");
		
		return "health";
	}
	
	@PostRequest("/deploy")
	public String deploy(@RequestBody DeployRequestDTO request) {
		logger.debug("Deploy service: {}", request);
		
		deployService.deployApplication(ApplicationMapper.buildApplicationFromDeployRequestDTO(request));
		
		return "/";
	}
	
//	@PostRequest("/rest-example")
//	public ImperatorResponse<DeployResponseDTO> restExample(@RequestBody DeployRequestDTO request) {
//		logger.debug("Deploy service: {}", request);
//		
//		DeployResponseDTO response = new DeployResponseDTO("Service deployed");
//		
//		return new ImperatorResponse<DeployResponseDTO>(200, response);
//	}
}