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
import com.sanjoyghosh.company.db.StringUtils;
import com.sanjoyghosh.company.model.Company;
import com.sanjoyghosh.company.model.EarningsDate;

public class YahooEarningsCalendarReader {

	private static final int MAX_RETRIES = 12;

	private EntityManager entityManager;
	private Map<String, Company> companyBySymbolMap;
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	
	
	private static Document fetchDocument(String url) throws IOException {
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

	
	// Static because JPMorganEarningsCalendar uses it to populate Yahoo information.
	public static void readSummaryYahoo(EarningsDate earningsDate) throws IOException {
		String aoyUrl = "http://finance.yahoo.com/q?s=" + earningsDate.getSymbol();
		Document doc = fetchDocument(aoyUrl);
		
		Elements elements = doc.select("td.yfnc_tabledata1");
		if (elements == null || elements.text() == null || elements.text().length() == 0) {
			System.err.println("No ticker for url: " + aoyUrl);
			return;
		}

		String marketCapStr = elements.get(11).text();		
		Long marketCap = StringUtils.parseLongWithBM(marketCapStr);
		earningsDate.setMarketCap(marketCap == null ? 0.0D : marketCap.longValue());
	}

	
	public static void readAnalystOpinionYahoo(EarningsDate earningsDate) throws IOException {
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
    	Timestamp timestamp = new Timestamp(date.getTime().getTime());
    	
		Document doc = fetchDocument(yepUrl);		
	    Elements trElements = doc.select("table[cellpadding=2").select("tr");
	    for (int i = 0; i < trElements.size(); i++) {
	    	Element trElement = trElements.get(i);
	    	Elements aElements = trElement.select("a[href^=http://finance.yahoo.com/q?s]");
	    	Elements smallElements = trElement.select("small");
	    	if (!aElements.isEmpty()) {
	    		String symbol = aElements.text();
	    		EarningsDate currentEarningsDate = CompanyUtils.fetchEarningsDateForSymbolDate(entityManager, symbol, timestamp);
	    		if (currentEarningsDate != null) {
	    			continue;
	    		}
	    		
	    		String releaseTime = smallElements.text();
	    		Company company = companyBySymbolMap.get(symbol);
	    		Integer companyId = company == null ? null : company.getId();
	    		
	    		EarningsDate earningsDate = new EarningsDate();
	    		earningsDate.setCompanyId(companyId);
	    		earningsDate.setSymbol(symbol);
	    		earningsDate.setEarningsDate(new Timestamp(date.getTime().getTime()));
	    		earningsDate.setBeforeMarketOrAfterMarket((releaseTime != null && releaseTime.indexOf("After Market Close") >= 0) ? "AM" : "BM");
	    		
	    		readAnalystOpinionYahoo(earningsDate);
	    		readSummaryYahoo(earningsDate);
	    		entityManager.persist(earningsDate);
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
