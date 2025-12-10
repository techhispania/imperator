package com.techhispania.imperator.server.services.factories;

import com.techhispania.imperator.server.services.DeployService;
import com.techhispania.imperator.server.services.impl.DeployServiceImpl;

public class DeployServiceFactory {

	public static DeployService createDeployServiceImpl() {
		return new DeployServiceImpl();
	}
}
