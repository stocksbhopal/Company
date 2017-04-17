package com.sanjoyghosh.company.db;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAHelper {

	private static EntityManagerFactory entityManagerFactory;
	private static List<String> mySQLHostList = Arrays.asList(
		"ec2-52-44-163-130.compute-1.amazonaws.com", 
		"ec2-34-195-18-116.compute-1.amazonaws.com"
	);
	
	
	private static EntityManagerFactory createEntityManagerFactory(String mysqlHost) {
		Map<String, String> mysqlProperties = null;
		if (mysqlHost != null) {
			String mysqlUrl = "jdbc:mysql://" + mysqlHost + ":3306/Company";
			mysqlProperties = new HashMap<String, String>();
			mysqlProperties.put("javax.persistence.jdbc.url", mysqlUrl);
			return Persistence.createEntityManagerFactory("com.sanjoyghosh.company.db.model", mysqlProperties);
		}
		else {
			return Persistence.createEntityManagerFactory("com.sanjoyghosh.company.db.model");
		}
	}
	
	
	// Use this method only for Batch jobs.
	public static EntityManager getEntityManager(String mysqlHost) {
		EntityManagerFactory emf = createEntityManagerFactory(mysqlHost);
		return emf.createEntityManager();
	}


	// Use this method for Web apps.
	public static EntityManager getEntityManager() {
		if (entityManagerFactory == null) {
			entityManagerFactory = createEntityManagerFactory(null);
		}
		return entityManagerFactory.createEntityManager();
	}


	public static List<String> getMySQLHostList() {
		return mySQLHostList;
	}
}
