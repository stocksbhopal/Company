package com.sanjoyghosh.company.yahoo;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.model.Company;
import com.sanjoyghosh.company.model.EarningsDate;

public class YahooEarningsCalendarReader {

	private static final int MAX_RETRIES = 12;

	private EntityManager entityManager;
	private Map<String, Company> companyBySymbolMap;
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	
	
	private Document fetchDocument(String url) throws IOException {
		Document doc = null;
		for (int i = 0; i < MAX_RETRIES; i++) {
			try {
				doc = Jsoup.connect(url).get();
				return doc;
			}
			catch (IOException e) {
				if (i == (MAX_RETRIES - 1)) {
					throw e;
				}
			}
		}
		return null;
	}

	
	private void readAnalystOpinionYahoo(EarningsDate earningsDate) throws IOException {
		String aoyUrl = "http://finance.yahoo.com/q/ao?s=" + earningsDate.getSymbol() + "+Analyst+Opinion";
		Document doc = fetchDocument(aoyUrl);
		Elements elements = doc.select("td.yfnc_tabledata1");

		if (elements.size() > 0) {
			for (int i = 0; i < 8; i++) {
				Element element = elements.get(i);
				String text = element.text().trim();
				if (text.equals("N/A")) {
					return;
				}
				text = text.replaceAll(",", "");
				try {
					switch(i) {
						case 0: earningsDate.setAnalystOpinion(Double.parseDouble(text)); break;
						case 1: break;
						case 2: break;
						case 3: break;
						case 4: break;
						case 5: break;
						case 6: break;
						case 7: earningsDate.setNumberBrokers(Integer.parseInt(text)); break;
					}
				}
				catch (NumberFormatException e) {
					System.out.println("Not a number: " + text);
				}
			}
	
		}
	}

	
	private void readEarningsCalendarFor(Calendar date) throws IOException {   
    	String yepUrl = "http://biz.yahoo.com/research/earncal/" + dateFormatter.format(date.getTime()) + ".html";
    	
		Document doc = fetchDocument(yepUrl);		
	    Elements trElements = doc.select("table[cellpadding=2").select("tr");
	    for (int i = 0; i < trElements.size(); i++) {
	    	Element trElement = trElements.get(i);
	    	
	    	Elements tdElements = trElement.select("td");
	    	if (!tdElements.isEmpty()) {
	    	}
	    	
	    	Elements aElements = trElement.select("a[href^=http://finance.yahoo.com/q?s]");
	    	Elements smallElements = trElement.select("small");
	    	if (!aElements.isEmpty()) {
	    		String symbol = aElements.text();
	    		String releaseTime = smallElements.text();
	    		Company company = companyBySymbolMap.get(symbol);
	    		Integer companyId = company == null ? null : company.getId();
	    		
	    		EarningsDate earningsDate = new EarningsDate();
	    		earningsDate.setCompanyId(companyId);
	    		earningsDate.setSymbol(symbol);
	    		earningsDate.setEarningsDate(new Timestamp(date.getTime().getTime()));
	    		earningsDate.setBeforeMarketOrAfterMarket((releaseTime != null && releaseTime.indexOf("After Market Close") >= 0) ? "AM" : "BM");
	    		
	    		readAnalystOpinionYahoo(earningsDate);
	    		System.out.println(earningsDate);
	    	}
	    }
    }

	
	private void readEarningsCalendarforWeek() {
		entityManager = JPAHelper.getEntityManager();
		companyBySymbolMap = CompanyUtils.fetchAllCompanyBySymbolMap(entityManager);
		
		Calendar calendar = new GregorianCalendar();
		calendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		for (int i = 0; i < 7; i++) {
			try {
				readEarningsCalendarFor(calendar);
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
			calendar.add(Calendar.DATE, 1);
		}		
	}
	
	
	public static void main(String[] args) {
		YahooEarningsCalendarReader reader = new YahooEarningsCalendarReader();
		reader.readEarningsCalendarforWeek();
		
		/*
		List<Stock> allStocks = StocksLib.findAllStocks();
		for (Stock stock : allStocks) {
			int stockId = stock.getId();
			String symbol = stock.getSymbol();
			
			try {
	    		AnalystOpinionYahoo aoy = AnalystOpinionYahooFetcher.fetchAnalystOpinionYahoo(symbol);
	    		if (aoy != null) {
	    			aoy.setStockId(stockId);
	    			aoy.setCreatedDate(todayInt);
	    			AnalystOpinionYahoo aoyInDB = StocksLib.findAnalystOpinionYahooByStockIdCreatedDate(stockId, todayInt);
	    			if (aoyInDB == null) {
		    			StocksLib.addAnalystOpinionYahoo(aoy);
	    			}
	    			else {
	    				aoy.setId(aoyInDB.getId());
	    				StocksLib.updateAnalystOpinionYahoo(aoy);
	    			}
	    		}
	    		
	    		QuoteYahoo qy = QuoteYahooFetcher.fetchQuoteYahoo(symbol);
	    		if (qy != null) {
    				qy.setStockId(stockId);
    				qy.setCreatedDate(todayInt);
	    			QuoteYahoo qyInDB = StocksLib.findQuoteYahooByStockIdCreatedDate(stockId, todayInt);
	    			if (qyInDB == null) {
	    				StocksLib.addQuoteYahoo(qy);
	    			}
	    			else {
	    				qy.setId(qyInDB.getId());
	    				StocksLib.updateQuoteYahoo(qy);
	    			}
	    		}
			}
			catch (Exception e) {
				logger.fatal("Exception in reading Yahoo Analyst Opinions and Quotes", e);
				System.exit(-1);
			}
		}
		
	    StocksLib.transactionCommit();
	    
	    FactSetEarningsEmail.processFactSetEarningsEmail();
	    */
		
		System.out.println("All Kosher, Good Night.");
		System.exit(0);
	}

}
