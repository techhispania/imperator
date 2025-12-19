package com.techhispania.imperator.server.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import jakarta.transaction.Transactional;

public class ApplicationServiceImpl implements ApplicationService {

	private static final Logger logger = LogManager.getLogger(ApplicationServiceImpl.class);
	
	private static final ApplicationRepository applicationRepository = RepositoriesFactory.createApplicationRepository();
	
	private static final CommandsService commandsService = CommandsServiceFactory.createCommandsServiceImpl();
	
	@Override
	public List<ApplicationDTO> getAllDeployedApplications() throws ImperatorException {
		List<Application> applications = applicationRepository.findAll();
		
		List<ApplicationDTO> result = new ArrayList<>();
		if (applications.size() > 0) {
			applications.forEach(a -> result.add(processApplication(a)));
			
		}
		return result;
	}
	
	@Override
	@Transactional
	public Optional<ApplicationDTO> checkApplicationStatus(String name) throws ImperatorException {
		Application application = applicationRepository.findBy("name", name);
		
		if (application == null) {
			logger.debug("No application found in database with name {}", name);
			return Optional.empty();
		}
		
		return Optional.of(processApplication(application));
	}

	private ApplicationDTO processApplication(Application application) {
		logger.info("Processing the application: {}", application.getName());
		
		String port = "";
		String status = "STOPPED";
		if (application.getPid() != null && application.getPid().length() > 0 && commandsService.isProcessRunning(application.getPid())) {
			status = "RUNNING";
			port = commandsService.getServicePort(application.getPid()).orElse("");
		} else {
			application.setPid(null);
			applicationRepository.update(application);
		}
		return new ApplicationDTO(application.getName(), port, status, application.getPid() != null ? application.getPid() : "");		
	}
}
