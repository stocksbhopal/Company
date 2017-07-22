package com.sanjoyghosh.company.source.nasdaq;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.PortfolioJPA;
import com.sanjoyghosh.company.db.model.PortfolioItem;
import com.sanjoyghosh.company.db.model.Price;

public class NasdaqRealtimePriceUpdater {

	private void updatePricesForPortfolios(EntityManager entityManager) {
		List<PortfolioItem> portfolioItems = PortfolioJPA.fetchAllPortfolioItems(entityManager);
		for (PortfolioItem item : portfolioItems) {
			NasdaqRealtimeQuote quote;
			try {
				quote = NasdaqRealtimeQuoteReader.fetchNasdaqStockSummary(item.getCompany().getSymbol());
				if (quote != null) {
					Price price = new Price();
					price.setCompanyId(item.getCompany().getId());
					price.setDateTime(LocalDateTime.now());
					price.setPrice(quote.getPrice());
					price.setPriceChange(quote.getPriceChange());
					price.setPriceChangePercent(quote.getPriceChangePercent());
					
					entityManager.persist(price);
				}
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static void main(String[] args) {
		Date startTime = new Date();
		EntityManager entityManager = null;
		for (String mySQLHost : JPAHelper.getMySQLHostList()) {
			try {
				entityManager = JPAHelper.getEntityManager(mySQLHost);
				NasdaqRealtimePriceUpdater reader = new NasdaqRealtimePriceUpdater();
				reader.updatePricesForPortfolios(entityManager);		
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				if (entityManager != null) {
					entityManager.close();
				}
			}
		}

		Date endTime = new Date();
		System.out.println("Time to persist Portfolio Item prices: " + ((endTime.getTime() - startTime.getTime()) / 1000L) + " secs");
		System.exit(0);
	}
}
