package com.techhispania.imperator.server.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techhispania.imperator.common.exceptions.ImperatorException;
import com.techhispania.imperator.server.domain.model.Application;
import com.techhispania.imperator.server.dto.ApplicationDTO;
import com.techhispania.imperator.server.infrastructure.db.repositories.ApplicationRepository;
import com.techhispania.imperator.server.infrastructure.db.repositories.RepositoriesFactory;
import com.techhispania.imperator.server.services.ApplicationService;
import com.techhispania.imperator.server.services.CommandsService;
import com.techhispania.imperator.server.services.factories.CommandsServiceFactory;

public class ApplicationServiceImpl implements ApplicationService {

	private static final Logger logger = LogManager.getLogger(ApplicationServiceImpl.class);
	
	private static final ApplicationRepository applicationRepository = RepositoriesFactory.createApplicationRepository();
	
	private static final CommandsService commandsService = CommandsServiceFactory.createCommandsServiceImpl();
	
	@Override
	public List<ApplicationDTO> getAllDeployedApplications() throws ImperatorException {
		
		List<Application> applications = applicationRepository.findAll();
		
		List<ApplicationDTO> result = new ArrayList<>();
		if (applications.size() > 0) {
			processApplication(applications, result);
		}
		return result;
	}

	private void processApplication(List<Application> applications, List<ApplicationDTO> result) {
		applications.forEach(a -> {
			logger.info("Processing the application: {}", a.getName());
			
			String port = "";
			String status = "STOPPED";
			if (a.getPid() != null && a.getPid().length() > 0 && commandsService.isProcessRunning(a.getPid())) {
				status = "RUNNING";
				port = commandsService.getServicePort(a.getPid()).orElse("");
			}
			ApplicationDTO dto = new ApplicationDTO(a.getName(), port, status, a.getPid() != null ? a.getPid() : "");
			result.add(dto);
		});
	}
}
