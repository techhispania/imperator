package com.techhispania.imperator.server.infrastructure.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

	private static EntityManagerFactory emf;
	
	public static void createEntityManagerFactory() {
		if (emf == null)
			emf = Persistence.createEntityManagerFactory("imperatorPU");
	}
	
	public static EntityManager getEntityManager() {
		return emf.createEntityManager();
	}

	public static void close() {
		emf.close();
	}
}
