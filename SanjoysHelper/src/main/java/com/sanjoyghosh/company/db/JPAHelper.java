package com.sanjoyghosh.company.db;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAHelper {

	private static EntityManagerFactory entityManagerFactory;
	
	
	private static void createEntityManagerFactory() {
		entityManagerFactory = Persistence.createEntityManagerFactory("com.sanjoyghosh.company.db.model");
	}

	public static EntityManager getEntityManager() {
		if (entityManagerFactory == null) {
			createEntityManagerFactory();
		}
		return entityManagerFactory.createEntityManager();
	}
}
