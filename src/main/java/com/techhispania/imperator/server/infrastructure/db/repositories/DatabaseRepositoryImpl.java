package com.techhispania.imperator.server.infrastructure.db.repositories;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techhispania.imperator.common.exceptions.ImperatorException;
import com.techhispania.imperator.common.utils.Constants;
import com.techhispania.imperator.server.infrastructure.db.JPAUtil;

import jakarta.persistence.EntityManager;

public abstract class DatabaseRepositoryImpl<T> implements DatabaseRepository<T> {

	private static final Logger logger = LogManager.getLogger(DatabaseRepository.class);
	
	private Class<T> entityClass;
	
	public DatabaseRepositoryImpl(Class<T> entityClass) {
		this.entityClass = entityClass;
	}
	
	public void save(T entity) throws ImperatorException {
		EntityManager em = JPAUtil.getEntityManager();
		try {
		    em.getTransaction().begin();

		    em.persist(entity);

		    em.getTransaction().commit();

		} catch (Exception e) {
			logger.error("Error saving entity.", e);
			throw new ImperatorException(Constants.HTTP_CODE_INTERNAL_SERVER_ERROR, String.format("Error saving entity. %s", e.getMessage()));
		} finally {
		    em.close();
		}
	}
	
	public List<T> findAll() throws ImperatorException {
		EntityManager em = JPAUtil.getEntityManager();
		
		String entityName = entityClass.getSimpleName();
		
		try {
			List<T> result = em.createQuery(String.format("SELECT e FROM %s e", entityName), entityClass).getResultList();
			logger.debug("Entities found for entity {}: {}", entityName, result.size());
			return result;
		} catch (Exception e) {
			logger.error("Error executing query.", e);
			throw new ImperatorException(Constants.HTTP_CODE_INTERNAL_SERVER_ERROR, String.format("Error executing query. %s", e.getMessage()));
		} finally {
			em.close();
		}
	}
}
