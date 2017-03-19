package com.sanjoyghosh.company.db;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAHelper {

	private static EntityManager entityManager;
	
	
	private static void createEntityManager() {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("com.sanjoyghosh.company.db.model");
		entityManager = entityManagerFactory.createEntityManager();
	}

	public static EntityManager getEntityManager() {
		if (entityManager == null) {
			createEntityManager();
		}
		return entityManager;
	}
}
