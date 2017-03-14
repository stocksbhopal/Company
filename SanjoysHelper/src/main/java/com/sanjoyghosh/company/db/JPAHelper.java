package com.sanjoyghosh.company.db;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

public class JPAHelper {

	private static EntityManager entityManager;
	private static EntityManager entityManagerLogs;
	
	
	private static void createEntityManager() {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("com.sanjoyghosh.company.db.model");
		entityManager = entityManagerFactory.createEntityManager();
	}

	private static void createEntityManagerLogs() {
		EntityManagerFactory entityManagerFactoryLogs = Persistence.createEntityManagerFactory("com.sanjoyghosh.company.logs");
		entityManagerLogs = entityManagerFactoryLogs.createEntityManager();
	}

	public static EntityManager getEntityManager() {
		if (entityManager == null) {
			createEntityManager();
		}
		return entityManager;
	}

	public static EntityManager getEntityManagerLogs() {
		if (entityManagerLogs == null) {
			createEntityManagerLogs();
		}
		return entityManagerLogs;
	}
}
