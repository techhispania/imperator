package com.techhispania.imperator.server.services.factories;

import com.techhispania.imperator.server.services.ApplicationService;
import com.techhispania.imperator.server.services.impl.ApplicationServiceImpl;

public class ApplicationServiceFactory {

	public static ApplicationService createApplicationServiceImpl() {
		return new ApplicationServiceImpl();
	}
}
