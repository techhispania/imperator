package com.techhispania.imperator.server.services.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techhispania.imperator.common.exceptions.ImperatorException;
import com.techhispania.imperator.common.utils.Constants;
import com.techhispania.imperator.server.domain.model.Application;
import com.techhispania.imperator.server.infrastructure.db.repositories.ApplicationRepository;
import com.techhispania.imperator.server.infrastructure.db.repositories.RepositoriesFactory;
import com.techhispania.imperator.server.services.DeployService;

public class DeployServiceImpl implements DeployService {

	private static final Logger logger = LogManager.getLogger(DeployServiceImpl.class);
	
	private static final String DEPLOYED_PATH = "../deployed/";
	
	private static final ApplicationRepository applicationRepository = RepositoriesFactory.createApplicationRepository();
	
	@Override
	public void deployApplication(Application application) throws ImperatorException {
		logger.info("Deploying application: {}", application);
		
		if (!validateApplication(application)) {
			throw new ImperatorException(Constants.HTTP_CODE_BAD_REQUEST, "Invalid Application received");
		}
		
		String applicationPath = new StringBuilder(DEPLOYED_PATH).append(application.getFilename()).toString();
		String command = String.format("nohup java -jar %s > /dev/null 2>&1 & echo $!", applicationPath);
		
		try {
			Process process = new ProcessBuilder("sh", "-c", command).start();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String pid = br.readLine();
			
			logger.info("Process executed: {}", pid);
			application.setPid(pid);
			
			logger.info("Saving application");
			applicationRepository.save(application);
		} catch (Exception e) {
			logger.error("Error executing java process", e);
		}
	}
	
	private boolean validateApplication(Application application) {
		boolean isValid = true;
		
		if (application == null)
			isValid = false;
		else if (application.getName() == null || application.getName().length() == 0)
			isValid = false;
		else if (application.getDescription() == null || application.getDescription().length() == 0)
			isValid = false;
		else if (application.getFilename() == null || application.getFilename().length() == 0)
			isValid = false;
		else if (application.getType() == null)
			isValid = false;
		
		return isValid;
	}
}
