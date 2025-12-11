package com.techhispania.imperator.server.infrastructure.db.repositories;

import com.techhispania.imperator.server.infrastructure.db.JPAUtil;

import jakarta.persistence.EntityManager;

public interface DatabaseRepository<T> {

	public default void insert(T entity) {
		EntityManager em = JPAUtil.getEntityManager();
		try {
		    em.getTransaction().begin();

		    em.persist(entity);

		    em.getTransaction().commit();

		} finally {
		    em.close();
		}
	}
}
