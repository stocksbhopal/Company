package com.sanjoyghosh.company.yahoohistorical;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.jsoup.Jsoup;

import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.model.Company;
import com.sanjoyghosh.company.model.PriceHistory;

public class YahooHistoricalPricesReader {

	private EntityManager entityManager;
	private SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");
	
	
	public YahooHistoricalPricesReader() {}
	
	
	private Map<String, Company> companyBySymbolMap;
	
	
	private Timestamp parseTimestamp(String str) {
		try {
			return new Timestamp(dateParser.parse(str).getTime());
		} 
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	private void readThePrices(String csvString, String symbol) {		
		Company company = companyBySymbolMap.get(symbol);
		int companyId = company == null ? 0 : company.getId();
		
		entityManager.getTransaction().begin();

		try {
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(new StringReader(csvString));
			for (CSVRecord record : records) {
				Timestamp dateOfPrice = parseTimestamp(record.get("Date"));
			    double openPrice = Double.parseDouble(record.get("Open"));
			    double closePrice = Double.parseDouble(record.get("Close"));
			    double lowPrice = Double.parseDouble(record.get("Low"));
			    double highPrice = Double.parseDouble(record.get("High"));
			    int volume = Integer.parseInt(record.get("Volume"));
			    			    
			    PriceHistory priceHistory = new PriceHistory();
			    priceHistory.setClosePrice(closePrice);
			    priceHistory.setCompanyId(companyId);
			    priceHistory.setDateOfPrice(dateOfPrice);
			    priceHistory.setHighPrice(highPrice);
			    priceHistory.setLowPrice(lowPrice);
			    priceHistory.setOpenPrice(openPrice);
			    priceHistory.setSymbol(symbol);
			    priceHistory.setVolume(volume);
			    
			    entityManager.persist(priceHistory);
			}
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		entityManager.getTransaction().commit();
	}
	
	
	@SuppressWarnings("deprecation")
	private void readHistoricalPrices(List<String> symbols) {
		for (String symbol : symbols) {
			int startDate = 01;
			int startMonth = 00;
			int startYear = 1995;
			PriceHistory priceHistory = CompanyUtils.fetchLastPriceHistoryForSymbol(entityManager, symbol);
			if (priceHistory != null) {
				startDate = priceHistory.getDateOfPrice().getDate();
				startMonth = priceHistory.getDateOfPrice().getMonth();
				startYear = priceHistory.getDateOfPrice().getYear() + 1900;
			}
			String url = "http://real-chart.finance.yahoo.com/table.csv?s=" + symbol + "&a=" + startMonth + "&b=" + startDate + "&c=" + startYear + "&d=11&e=31&f=2020&g=d&ignore=.csv";
			System.out.println(url);
			try {
				String csvString = Jsoup.connect(url).execute().body();
				readThePrices(csvString, symbol);
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	private void readAllHistoricalPrices() {
		entityManager = JPAHelper.getEntityManager();
		companyBySymbolMap = CompanyUtils.fetchAllCompanyBySymbolMap(entityManager);
		
		List<String> holdingSymbols = CompanyUtils.fetchAllHoldingSymbols(entityManager);
		readHistoricalPrices(holdingSymbols);
		
		List<String> companySymbols = CompanyUtils.fetchAllCompanySymbols(entityManager);
		readHistoricalPrices(companySymbols);
	}
	
	
	public static void main(String[] args) {
		YahooHistoricalPricesReader reader = new YahooHistoricalPricesReader();
		reader.readAllHistoricalPrices();
	}
}
