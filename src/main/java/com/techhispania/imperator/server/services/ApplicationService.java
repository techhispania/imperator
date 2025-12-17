package com.techhispania.imperator.server.services;

import java.util.List;
import java.util.Optional;

import com.techhispania.imperator.common.exceptions.ImperatorException;
import com.techhispania.imperator.server.dto.ApplicationDTO;

public interface ApplicationService {

	/**
	 * Get from database all the deployed applications with the status
	 * and the port
	 * 
	 * @return List of deployed applications
	 * @throws ImperatorException
	 */
	List<ApplicationDTO> getAllDeployedApplications() throws ImperatorException;
	
	/**
	 * Checks the current status of one application
	 * 
	 * @param name The name of the application
	 * @return The application status
	 * @throws ImperatorException
	 */
	Optional<ApplicationDTO> checkApplicationStatus(String name) throws ImperatorException;
}
