package com.sanjoyghosh.company.source.yahoo;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.EarningsDate;
import com.sanjoyghosh.company.utils.JsoupUtils;

public class YahooEarningsCalendarReader {

	private EntityManager entityManager;
	private Map<String, Company> companyBySymbolMap;
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	
		
	private void readEarningsCalendarFor(Calendar date) throws IOException {   
    	String yepUrl = "http://biz.yahoo.com/research/earncal/" + dateFormatter.format(date.getTime()) + ".html";
    	Timestamp timestamp = new Timestamp(date.getTime().getTime());
    	
		Document doc = JsoupUtils.fetchDocument(yepUrl);		
		System.out.println("Fetch: " + yepUrl);
	    Elements trElements = doc.select("table[cellpadding=2").select("tr");
	    for (int i = 0; i < trElements.size(); i++) {
	    	Element trElement = trElements.get(i);
	    	Elements aElements = trElement.select("a[href^=http://finance.yahoo.com/q?s]");
	    	Elements smallElements = trElement.select("small");
	    	System.out.println(aElements);
	    	if (!aElements.isEmpty()) {
	    		String symbol = aElements.text();
	    		if ((symbol.indexOf('^') >= 0) || (symbol.indexOf('.') >= 0)) {
	    			continue;
	    		}
	    		
	    		boolean updateEarningsDate = false;
	    		EarningsDate currentEarningsDate = CompanyUtils.fetchEarningsDateForSymbolDate(entityManager, symbol, timestamp);
	    		if (currentEarningsDate != null) {
	    			updateEarningsDate = true;
	    		}
	    		
	    		String releaseTime = smallElements.text();
	    		Company company = companyBySymbolMap.get(symbol);
	    		Integer companyId = company == null ? null : company.getId();
	    		
	    		EarningsDate earningsDate = updateEarningsDate ? currentEarningsDate : new EarningsDate();
	    		earningsDate.setCompanyId(companyId);
	    		earningsDate.setSymbol(symbol);
	    		earningsDate.setEarningsDate(new Timestamp(date.getTime().getTime()));
	    		earningsDate.setBeforeMarketOrAfterMarket((releaseTime != null && releaseTime.indexOf("After Market Close") >= 0) ? "AM" : "BM");
	    		
	    		YahooStockSummary summary = YahooStockSummaryPage.fetchYahooStockSummary(symbol);
	    		earningsDate.setMarketCap(summary == null ? null : summary.getMarketCap());

	    		YahooAnalystOpinion opinion = YahooAnalystOpinionPage.fetchAnalystOpinionYahoo(symbol);
	    		if (opinion != null) {
	    			earningsDate.setAnalystOpinion(opinion.getMeanRecommendationThisWeek());
	    			earningsDate.setNumberBrokers(opinion.getNumberOfBrokers());
	    		}
	    		System.out.println("Earnings Cal: " + earningsDate);

	    		if (!updateEarningsDate) {
	    			entityManager.persist(earningsDate);
	    		}
	    		else {
	    			entityManager.merge(earningsDate);
	    		}
	    	}
	    }   
    }

	
	public void readEarningsCalendarforWeek() {
		entityManager = JPAHelper.getEntityManager();
		companyBySymbolMap = CompanyUtils.fetchAllCompanyBySymbolMap(entityManager);
		
		Calendar calendar = new GregorianCalendar();
		calendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		for (int i = 0; i < 7; i++) {
			try {
		    	entityManager.getTransaction().begin();		    	
				readEarningsCalendarFor(calendar);
			    entityManager.getTransaction().commit();
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				entityManager.getTransaction().rollback();
			}
			calendar.add(Calendar.DATE, 1);
		}		
	}
	
	
	public static void main(String[] args) {
		YahooEarningsCalendarReader reader = new YahooEarningsCalendarReader();
		reader.readEarningsCalendarforWeek();		
		System.exit(0);
	}
}
