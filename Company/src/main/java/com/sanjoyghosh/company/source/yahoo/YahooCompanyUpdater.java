package com.sanjoyghosh.company.source.yahoo;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;

import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;

public class YahooCompanyUpdater {

	private EntityManager entityManager;
	
	
	private void updateMarketCapForAllCompanies() {
		entityManager = JPAHelper.getEntityManager();
		List<Company> companyList = CompanyUtils.fetchAllCompany(entityManager);
		
		int count = 0;
		int totalCompanies = companyList.size();
		
		entityManager.getTransaction().begin();
		for (Company company : companyList) {
			try {
				YahooStockSummary summary = YahooStockSummaryPage.fetchYahooStockSummary(company.getSymbol());
				if (summary != null) {
					company.setMarketCap(summary.getMarketCap());
					company.setMarketCapBM(summary.getMarketCapBM());
					entityManager.persist(company);
				}
			} 
			catch (IOException e) {
				e.printStackTrace();
				entityManager.getTransaction().rollback();
				return;
			}
			
			count++;
			System.out.println("Done " + company.getSymbol() + ", " + count + " of " + totalCompanies);
		}
		entityManager.getTransaction().commit();
	}
	
	
	public static void main(String[] args) {
		YahooCompanyUpdater updater = new YahooCompanyUpdater();
		updater.updateMarketCapForAllCompanies();
		System.exit(0);
	}
}
