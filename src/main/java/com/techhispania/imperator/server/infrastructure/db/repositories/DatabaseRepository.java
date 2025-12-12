package com.techhispania.imperator.server.infrastructure.db.repositories;

import java.util.List;

public interface DatabaseRepository<T> {
	
	public void save(T entity);
	
	public List<T> findAll();
}
