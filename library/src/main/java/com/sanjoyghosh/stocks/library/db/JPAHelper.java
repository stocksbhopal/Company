package com.sanjoyghosh.stocks.library.db;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAHelper {

	public static void createEntityManager() {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("com.sanjoyghosh.stocks.library.model");
		System.out.println("FACTORY CREATED CLEAN: " + factory);
	}
}
