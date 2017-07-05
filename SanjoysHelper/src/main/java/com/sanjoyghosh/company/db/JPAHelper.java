package com.sanjoyghosh.company.db;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.sanjoyghosh.company.utils.AWSUtils;
import com.sanjoyghosh.company.utils.HostTypeEnum;

public class JPAHelper {

    private static final Logger logger = Logger.getLogger(JPAHelper.class.getName());

	private static EntityManagerFactory entityManagerFactory;
	private static List<String> mySQLHostList = Arrays.asList(
		"ec2-52-44-163-130.compute-1.amazonaws.com", 
		"ec2-34-195-18-116.compute-1.amazonaws.com"
	);
	
	
	private static String getMysqlHost() {
		HostTypeEnum hostTypeEnum = AWSUtils.getHostTypeEnum();
		if (hostTypeEnum == HostTypeEnum.FINANCE_HELPER || hostTypeEnum == HostTypeEnum.FINANCE_HELPER_DEV) {
			return "localhost";
		}
		if (hostTypeEnum == HostTypeEnum.DEV_BOX) {
			return HostTypeEnum.FINANCE_HELPER_DEV.getPublicHostName();
		}
		return null;
	}
	
	
	private static EntityManagerFactory createEntityManagerFactory(String mysqlHost) {
		Map<String, String> mysqlProperties = null;
		if (mysqlHost == null) {
			mysqlHost = getMysqlHost();
		}
				
		if (mysqlHost == null) {
			logger.severe("NO MYSQL HOST NAME. VERY, VERY, VERY BAD.");
		}
		assert mysqlHost != null;

		String mysqlUrl = "jdbc:mysql://" + mysqlHost + ":3306/Company";
		mysqlProperties = new HashMap<String, String>();
		mysqlProperties.put("javax.persistence.jdbc.url", mysqlUrl);
		return Persistence.createEntityManagerFactory("com.sanjoyghosh.company.db.model", mysqlProperties);
	}
	
	
	// Use this method only for Batch jobs.
	public static EntityManager getEntityManager(String mySQLHost) {
		EntityManagerFactory emf = createEntityManagerFactory(mySQLHost);
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
