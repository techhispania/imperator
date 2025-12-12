package com.techhispania.imperator.server.infrastructure.db.repositories;

import com.techhispania.imperator.server.domain.model.Application;

public class RepositoriesFactory {

	public static ApplicationRepository createApplicationRepository() {
		return new ApplicationRepository(Application.class);
	}
}
