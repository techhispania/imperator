package com.techhispania.imperator.server.dto.mappers;

import com.techhispania.imperator.server.domain.model.Application;
import com.techhispania.imperator.server.domain.model.ApplicationType;
import com.techhispania.imperator.server.dto.DeployRequestDTO;

public class ApplicationMapper {
	
	public static Application buildApplicationFromDeployRequestDTO(DeployRequestDTO dto) {
		Application application = new Application();
		application.setName(dto.applicationName());
		application.setDescription(dto.description());
		application.setType(ApplicationType.fromValue(dto.applicationType()));
		application.setFilename(dto.filename());
		return application;
	}
}
