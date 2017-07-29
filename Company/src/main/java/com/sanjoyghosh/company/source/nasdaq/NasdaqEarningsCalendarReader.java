package com.sanjoyghosh.company.source.nasdaq;

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

import com.sanjoyghosh.company.db.CompanyDBUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.EarningsDate;
import com.sanjoyghosh.company.utils.JsoupUtils;
import com.sanjoyghosh.company.utils.LocalDateUtils;

public class NasdaqEarningsCalendarReader {

	private Map<String, Company> companyBySymbolMap;
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
	
		
	private int readEarningsCalendarFor(LocalDate date, EntityManager entityManager) throws IOException {   
    	String yepUrl = "http://www.nasdaq.com/earnings/earnings-calendar.aspx?date=" + date.format(dateFormatter);
    	
		Document doc = null;
		try {
			doc = JsoupUtils.fetchDocument(yepUrl);	
		}
		catch (HttpStatusException e) {
			System.err.println("No doc for: " + yepUrl + ", status: " + e.getStatusCode());
			return -1;
		}
		
		int count = 0;
	    Elements aElements = doc.select("table[class=USMN_EarningsCalendar").select("a");
	    for (int i = 0; i < aElements.size(); i++) {
	    	Element aElement = aElements.get(i);
	    	String id = aElement.attr("id");
	    	if (id.startsWith("two_column_main_content_CompanyTable_companyname")) {
	    		int numEarningsEstimates = Integer.parseInt(
	    			aElement.parent().nextElementSibling().nextElementSibling().nextElementSibling().nextElementSibling().text());

	    		String amBm = "BM";
	    		Elements prevChildren = aElement.parent().previousElementSibling().children();
	    		if (prevChildren.size() > 0) {
	    			String amBmTitle = prevChildren.get(0).attr("title");
	    			amBm = amBmTitle.equals("Pre-market Quotes") ? "BM" : "AM";
	    		}
	    		
	    		String symbol = aElement.text();
	    		int pos0 = symbol.indexOf("Market Cap:");
	    		int pos1 = symbol.lastIndexOf('(', pos0);
	    		int pos2 = symbol.lastIndexOf(')', pos0);
	    		symbol = symbol.substring(pos1 + 1, pos2);
	    		if ((symbol.indexOf('^') >= 0) || (symbol.indexOf('.') >= 0)) {
	    			continue;
	    		}
	    		Company company = companyBySymbolMap.get(symbol);
	    		if (company == null) {
	    			System.out.println("NO COMPANY FOUND FOR: " + aElement.text());
	    			continue;
	    		}
	    			    			    		
	    		boolean hasEarningsDate = false;
	    		List<EarningsDate> earningsDateList = CompanyDBUtils.fetchEarningsDateListForSymbolDate(entityManager, symbol, date);
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
		    		earningsDate.setBeforeMarketOrAfterMarket(amBm);	
		    		entityManager.persist(earningsDate);
		    		
		    		company.setNumEarningsEstimates(numEarningsEstimates);
		    		NasdaqCompanyUpdater.updateCompany(company);
		    		entityManager.persist(company);

		    		count++;
	    		}
	    	}
	    }   
	    return count;
    }

	
	private void readEarningsCalendarforMonth(EntityManager entityManager) {
		companyBySymbolMap = CompanyDBUtils.fetchAllCompanyBySymbolMap(entityManager);
		LocalDate date = LocalDate.now();
		for (int i = 0; i < 31; i++) {
			try {
		    	entityManager.getTransaction().begin();		    	
				int count = readEarningsCalendarFor(date, entityManager);
			    entityManager.getTransaction().commit();
				System.out.println("Got " + count + " earnings for " + LocalDateUtils.toDateString(date));				
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
		for (String mySQLHost : JPAHelper.getMySQLHostList()) {
			try {
				entityManager = JPAHelper.getEntityManager(mySQLHost);
				NasdaqEarningsCalendarReader reader = new NasdaqEarningsCalendarReader();
				reader.readEarningsCalendarforMonth(entityManager);		
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

		System.exit(0);
	}
}
