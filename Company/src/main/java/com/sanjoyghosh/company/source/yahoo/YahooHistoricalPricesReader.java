package com.sanjoyghosh.company.source.yahoo;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.PriceHistory;
import com.sanjoyghosh.company.utils.CompanyUtils;

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
				Timestamp dateOfPrice = parseTimestamp(record.get("Date").trim());
			    double openPrice = Double.parseDouble(record.get("Open").trim());
			    double closePrice = Double.parseDouble(record.get("Close").trim());
			    double lowPrice = Double.parseDouble(record.get("Low").trim());
			    double highPrice = Double.parseDouble(record.get("High").trim());
			    
			    int volume = 0;
			    try {
			    	volume = Integer.parseInt(record.get("Volume").trim());
			    }
			    catch (NumberFormatException e) {System.err.println("Cannot parse as int volume: " + record.get("Volume") + " for symbol: " + symbol);}
			    			    
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
		int totalSymbols = symbols.size();
		int count = 0;
		for (String symbol : symbols) {
			Calendar calendar = new GregorianCalendar(1995, 0, 1);
			PriceHistory priceHistory = CompanyUtils.fetchLastPriceHistoryForSymbol(entityManager, symbol);
			if (priceHistory != null) {
				calendar = new GregorianCalendar(priceHistory.getDateOfPrice().getYear() + 1900, priceHistory.getDateOfPrice().getMonth(), priceHistory.getDateOfPrice().getDate(), 0, 0);
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
			String url = "http://real-chart.finance.yahoo.com/table.csv?s=" + URLEncoder.encode(symbol) + "&a=" + calendar.get(Calendar.MONTH) + "&b=" + calendar.get(Calendar.DAY_OF_MONTH) + "&c=" + calendar.get(Calendar.YEAR) + "&d=11&e=31&f=2020&g=d&ignore=.csv";
			System.out.println(url);
			try {
				String csvString = Jsoup.connect(url).execute().body();
				readThePrices(csvString, symbol);
			} 
			catch (HttpStatusException e) {
				System.out.println("No new prices for " + symbol);
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				count++;
				System.out.println("Done " + symbol + ", " + count + " of " + totalSymbols);
			}
		}
	}
	
	
	public void readAllHistoricalPrices() {
		entityManager = JPAHelper.getEntityManager();
		companyBySymbolMap = CompanyUtils.fetchAllCompanyBySymbolMap(entityManager);
		
		List<String> holdingSymbols = CompanyUtils.fetchAllHoldingsSymbols(entityManager);
		readHistoricalPrices(holdingSymbols);
		
		List<String> companySymbols = CompanyUtils.fetchAllCompanySymbols(entityManager);
		readHistoricalPrices(companySymbols);
	}
	
	
	public static void main(String[] args) {
		YahooHistoricalPricesReader reader = new YahooHistoricalPricesReader();
		reader.readAllHistoricalPrices();
		System.exit(0);
	}
}
