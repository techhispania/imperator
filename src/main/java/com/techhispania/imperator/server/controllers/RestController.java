package com.techhispania.imperator.server.controllers;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techhispania.imperator.common.annotations.Controller;
import com.techhispania.imperator.common.annotations.PostRequest;
import com.techhispania.imperator.common.annotations.RequestBody;
import com.techhispania.imperator.common.exceptions.ImperatorException;
import com.techhispania.imperator.common.utils.Constants;
import com.techhispania.imperator.core.http.dto.ImperatorResponse;
import com.techhispania.imperator.server.dto.ApplicationDTO;
import com.techhispania.imperator.server.dto.CheckServiceStatusRequest;
import com.techhispania.imperator.server.dto.CheckServiceStatusResponse;
import com.techhispania.imperator.server.services.ApplicationService;
import com.techhispania.imperator.server.services.factories.ApplicationServiceFactory;

@Controller
public class RestController {

	private static final Logger logger = LogManager.getLogger(RestController.class);
	
	private static final ApplicationService applicationService = ApplicationServiceFactory.createApplicationServiceImpl();
	
	@PostRequest("/api/service/check")
	public ImperatorResponse<CheckServiceStatusResponse> checkServiceStatus(@RequestBody CheckServiceStatusRequest request) {
		logger.debug("Check Service status request: {}", request);
		
		Optional<ApplicationDTO> applicationStatus = applicationService.checkApplicationStatus(request.serviceName());
		
		if (applicationStatus.isEmpty()) {
			throw new ImperatorException(Constants.HTTP_CODE_NOT_FOUND, String.format("Application '%s' not found"));
		}
		
		CheckServiceStatusResponse response = new CheckServiceStatusResponse(applicationStatus.get().name(), 
																			applicationStatus.get().status(), 
																			applicationStatus.get().port(), 
																			applicationStatus.get().pid());
		
		return new ImperatorResponse<CheckServiceStatusResponse>(Constants.HTTP_CODE_SUCCESS, response);
	}
}
