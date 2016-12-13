package com.sanjoyghosh.company.source.nasdaq;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.Price;

public class NasdaqPriceUpdater {

	public static void main (String[] args) {
		long startTime = System.currentTimeMillis();
		
		LocalDateTime dateTime = LocalDateTime.now();
		EntityManager entityManager = JPAHelper.getEntityManager();
		
		try {
			entityManager.getTransaction().begin();
			
			Map<Integer, Company> companyByIdMap = CompanyUtils.fetchAllCompanyByIdMap(entityManager);
			
			List<Price> priceList = CompanyUtils.fetchPriceList(entityManager);
			for (Price price : priceList) {
				Company company = companyByIdMap.get(price.getCompanyId());
				if (company != null) {
					NasdaqRealtimeQuote quote = NasdaqRealtimeQuoteReader.fetchNasdaqStockSummary(company.getSymbol());
					if (quote != null) {
						price.setDateTime(dateTime);
						price.setPrice(quote.getPrice());
						price.setPriceChange(quote.getPriceChange());
						price.setPriceChangePercent(quote.getPriceChangePercent());
						entityManager.persist(price);
					}
				}
			}
			
			List<Company> companyList = CompanyUtils.fetchCompanyListForAllUsersMinusPrice(entityManager);
			for (Company company : companyList) {
				NasdaqRealtimeQuote quote = NasdaqRealtimeQuoteReader.fetchNasdaqStockSummary(company.getSymbol());
				if (quote != null) {
					Price price = new Price();
					price.setCompanyId(company.getId());
					price.setDateTime(dateTime);
					price.setPrice(quote.getPrice());
					price.setPriceChange(quote.getPriceChange());
					price.setPriceChangePercent(quote.getPriceChangePercent());
					entityManager.persist(price);					
				}
			}
			
			entityManager.getTransaction().commit();
		}
		catch (Exception e) {
			e.printStackTrace();
			entityManager.getTransaction().rollback();
		}
		
		long endTime = System.currentTimeMillis();
		System.out.println("Total time: " + ((endTime - startTime) / 1000L) + " seconds");
		System.exit(0);
	}
}
