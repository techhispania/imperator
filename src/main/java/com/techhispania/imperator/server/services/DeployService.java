package com.techhispania.imperator.server.services;

import com.techhispania.imperator.common.exceptions.ImperatorException;
import com.techhispania.imperator.server.domain.model.Application;

public interface DeployService {

	/**
	 * Deploy an application received from the web form
	 * 
	 * @param application The entity that contains all the needed information to
	 *                    deploy one application
	 * @throws ImperatorException
	 */
	void deployApplication(Application application) throws ImperatorException;
}
