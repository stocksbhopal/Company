package com.sanjoyghosh.company.db;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAHelper {

	private static EntityManager entityManager;
	
	private static void createEntityManager() {
		/*
		Logger log = Logger.getLogger("org.hibernate.*");
		log.setLevel(Level.SEVERE);
		
	    org.jboss.logging.Logger logger = org.jboss.logging.Logger.getLogger("org.hibernate");
	    java.util.logging.Logger.getLogger("org.hibernate").setLevel(java.util.logging.Level.SEVERE); //or whatever level you need
	    */
		
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("com.sanjoyghosh.stocks.library.model");
		entityManager = entityManagerFactory.createEntityManager();
	}

	public static EntityManager getEntityManager() {
		if (entityManager == null) {
			createEntityManager();
		}
		return entityManager;
	}
}
