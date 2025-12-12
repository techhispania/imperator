package com.techhispania.imperator.server.infrastructure.db.repositories;

import com.techhispania.imperator.server.domain.model.Application;

public class ApplicationRepository extends DatabaseRepositoryImpl<Application> {

	public ApplicationRepository(Class<Application> entityClass) {
		super(entityClass);
	}
}
