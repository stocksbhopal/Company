package com.sanjoyghosh.company.source.yahoo;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.EarningsDate;
import com.sanjoyghosh.company.source.nasdaq.NasdaqCompanyUpdater;
import com.sanjoyghosh.company.utils.DateUtils;
import com.sanjoyghosh.company.utils.JsoupUtils;

public class YahooEarningsCalendarReader {

	private Map<String, Company> companyBySymbolMap;
	private DateTimeFormatter dateFormatter = DateTimeFormatter.BASIC_ISO_DATE;
	
		
	private void readEarningsCalendarFor(LocalDate date, EntityManager entityManager) throws IOException {   
    	String yepUrl = "http://biz.yahoo.com/research/earncal/" + date.format(dateFormatter) + ".html";
    	
		Document doc = null;
		try {
			doc = JsoupUtils.fetchDocument(yepUrl);	
		}
		catch (HttpStatusException e) {
			System.err.println("No doc for: " + yepUrl + ", status: " + e.getStatusCode());
			return;
		}
		
	    Elements trElements = doc.select("table[cellpadding=2").select("tr");
	    for (int i = 0; i < trElements.size(); i++) {
	    	Element trElement = trElements.get(i);
	    	Elements aElements = trElement.select("a[href^=http://finance.yahoo.com/q?s]");
	    	Elements smallElements = trElement.select("small");
	    	if (!aElements.isEmpty()) {
	    		String symbol = aElements.text();
	    		if ((symbol.indexOf('^') >= 0) || (symbol.indexOf('.') >= 0)) {
	    			continue;
	    		}
	    		Company company = companyBySymbolMap.get(symbol);
	    		if (company == null) {
	    			continue;
	    		}

	    		NasdaqCompanyUpdater.updateCompany(company);
	    		entityManager.persist(company);
	    		
	    		String releaseTime = smallElements.text();
	    		releaseTime = (releaseTime != null && releaseTime.indexOf("After Market Close") >= 0) ? "AM" : "BM";
	    		
	    		boolean hasEarningsDate = false;
	    		List<EarningsDate> earningsDateList = CompanyUtils.fetchEarningsDateListForSymbolDate(entityManager, symbol, date);
	    		for (EarningsDate earningsDate : earningsDateList) {
	    			if (!earningsDate.getEarningsDate().equals(date)) {
	    				entityManager.remove(earningsDate);
	    			}
	    			else {
	    				hasEarningsDate = true;
	    			}
	    		}

	    		if (!hasEarningsDate) {
		    		EarningsDate earningsDate = new EarningsDate();
		    		earningsDate.setCompanyId(company.getId());
		    		earningsDate.setSymbol(symbol);
		    		earningsDate.setEarningsDate(date);
		    		earningsDate.setBeforeMarketOrAfterMarket(releaseTime);

		    		entityManager.persist(earningsDate);
		    		continue;
	    		}
	    	}
	    }   
    }

	
	private void readEarningsCalendarforMonth(EntityManager entityManager) {
		companyBySymbolMap = CompanyUtils.fetchAllCompanyBySymbolMap(entityManager);
		LocalDate date = LocalDate.now();
		for (int i = 0; i < 31; i++) {
			try {
				System.out.println("Getting earnings for " + DateUtils.toDateString(date));				
		    	entityManager.getTransaction().begin();		    	
				readEarningsCalendarFor(date, entityManager);
			    entityManager.getTransaction().commit();
			} 
			catch (IOException e) {
				e.printStackTrace();
				if (entityManager.getTransaction().isActive()) {
					entityManager.getTransaction().rollback();
				}
				return;
			}
			date = date.plusDays(1);
		}		
	}
	
	
	public static void main(String[] args) {
		EntityManager entityManager = null;
		
		try {
			entityManager = JPAHelper.getEntityManager("ec2-52-44-163-130.compute-1.amazonaws.com");
			YahooEarningsCalendarReader reader = new YahooEarningsCalendarReader();
			reader.readEarningsCalendarforMonth(entityManager);		
		}
		finally {
			if (entityManager != null) {
				entityManager.close();
			}
		}

		try {
			entityManager = JPAHelper.getEntityManager("ec2-34-195-18-116.compute-1.amazonaws.com");
			YahooEarningsCalendarReader reader = new YahooEarningsCalendarReader();
			reader.readEarningsCalendarforMonth(entityManager);		
		}
		finally {
			if (entityManager != null) {
				entityManager.close();
			}
		}
		
		System.exit(0);
	}
}
