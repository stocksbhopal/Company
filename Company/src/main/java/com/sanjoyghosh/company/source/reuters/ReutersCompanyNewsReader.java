package com.sanjoyghosh.company.source.reuters;

import java.util.List;

import javax.persistence.EntityManager;

import com.sanjoyghosh.company.db.JPAHelper;

public class ReutersCompanyNewsReader {

	private void readAllPortfolioCompanyNews(EntityManager entityManager) {
//		https://www.reuters.com/finance/stocks/companyNews?symbol=IBM.N&date=07232017
//		https://www.google.com/finance/company_news?q=NASDAQ%3AAMZN&startdate=2017-7-22&enddate=2017-8-01
	}

	
	public static void main(String[] args) {
		List<String> mySQLHostList = JPAHelper.getMySQLHostList();
		for (String mySQLHost : mySQLHostList) {
			EntityManager entityManager = null;
			try {
				entityManager = JPAHelper.getEntityManager(mySQLHost);
				entityManager.getTransaction().begin();
				
				ReutersCompanyNewsReader reader = new ReutersCompanyNewsReader();
				reader.readAllPortfolioCompanyNews(entityManager);
				
				entityManager.getTransaction().commit();
			}
			catch (Exception e) {
				e.printStackTrace();
				if (entityManager.getTransaction().isActive()) {
					entityManager.getTransaction().rollback();
				}
			}
			finally {
				if (entityManager != null) {
					entityManager.close();
				}
			}
		}
		
		System.exit(0);
	}
}
