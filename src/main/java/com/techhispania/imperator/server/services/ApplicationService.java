package com.techhispania.imperator.server.services;

import java.util.List;

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
}
