package com.techhispania.imperator.server.services.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techhispania.imperator.common.exceptions.ImperatorException;
import com.techhispania.imperator.server.domain.model.Application;
import com.techhispania.imperator.server.services.DeployService;

public class DeployServiceImpl implements DeployService {

	private static final Logger logger = LogManager.getLogger(DeployServiceImpl.class);
	
	@Override
	public void deployApplication(Application application) throws ImperatorException {
		logger.info("Deploying application: {}", application);
		
	}
}
