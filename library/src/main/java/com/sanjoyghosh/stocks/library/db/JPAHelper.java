package com.sanjoyghosh.stocks.library.db;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAHelper {

	private static EntityManager entityManager;
	
	public static void createEntityManager() {
		Logger log = Logger.getLogger("org.hibernate.SQL");
		log.setLevel(Level.SEVERE);
		
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("com.sanjoyghosh.stocks.library.model");
		entityManager = entityManagerFactory.createEntityManager();
		System.out.println("ENTITY MANAGER DONE");
	}

	public static EntityManager getEntityManager() {
		return entityManager;
	}
}
