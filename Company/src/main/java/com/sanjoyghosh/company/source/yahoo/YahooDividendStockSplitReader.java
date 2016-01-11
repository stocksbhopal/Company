package com.sanjoyghosh.company.source.yahoo;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.DividendHistory;
import com.sanjoyghosh.company.db.model.StockSplitHistory;

public class YahooDividendStockSplitReader {

	private EntityManager entityManager;
	private Map<String, Company> companyBySymbolMap;

	private SimpleDateFormat dateParser = new SimpleDateFormat("yyyyMMdd");
	
	
	public YahooDividendStockSplitReader() {}
		
	
	private Timestamp parseTimestamp(String str) {
		try {
			return new Timestamp(dateParser.parse(str).getTime());
		} 
		catch (ParseException e) {
			return null;
		}
	}
	
	
	private void readTheDividendsAndSplits(String csvString, String symbol, int companyId) {		
		entityManager.getTransaction().begin();

		try {
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(new StringReader(csvString));
			for (CSVRecord record : records) {
				String typeOfEvent = record.get(0).trim();
				Timestamp date = parseTimestamp(record.get(1).trim());
				
				if (typeOfEvent.startsWith("DIVIDEND")) {
					DividendHistory dividendHistory = CompanyUtils.fetchDividendHistoryForSymbolDate(entityManager, symbol, date);
					if (dividendHistory == null) {
						dividendHistory = new DividendHistory();
						
						dividendHistory.setCompanyId(companyId);
						dividendHistory.setDateOfDividend(date);
						dividendHistory.setDividend(Double.parseDouble(record.get(2).trim()));
						dividendHistory.setSymbol(symbol);
						
						entityManager.persist(dividendHistory);
					}
					continue;
				}
				
				if (typeOfEvent.startsWith("SPLIT")) {
					StockSplitHistory splitHistory = CompanyUtils.fetchStockSplitHistoryForSymbolDate(entityManager, symbol, date);
					if (splitHistory == null) {
						String[] ratioStrs = record.get(2).trim().split(":");
						double splitRatio = (new Integer(ratioStrs[0]).doubleValue()) / (new Integer(ratioStrs[1]).doubleValue());
	
						splitHistory = new StockSplitHistory();
						
						splitHistory.setCompanyId(companyId);
						splitHistory.setDateOfSplit(date);
						splitHistory.setSplitRatio(splitRatio);
						splitHistory.setSymbol(symbol);
						
						entityManager.persist(splitHistory);
					}
					continue;
				}
				
				if (typeOfEvent.startsWith("STARTDATE") || typeOfEvent.startsWith("ENDDATE") || 
					typeOfEvent.startsWith("TOTALSIZE") || typeOfEvent.startsWith("STATUS")) {
					// Ignore these.
					continue;
				}
				System.err.println("Unknown Type in CSV: " + record.get(0));
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		entityManager.getTransaction().commit();
	}
	
	
	private void readHistoricalDividendsAndSplits() {
		int totalSymbols = companyBySymbolMap.size();
		int count = 0;
		for (Company company : companyBySymbolMap.values()) {
			String url = "http://real-chart.finance.yahoo.com/x?s=" + company.getSymbol() + "&a=00&b=1&c=1995&d=11&e=31&f=2020&g=v";
			System.out.println(url);
			try {
				String csvString = Jsoup.connect(url).execute().body();
				readTheDividendsAndSplits(csvString, company.getSymbol(), company.getId());
			} 
			catch (HttpStatusException e) {
				System.out.println("No new prices for " + company.getSymbol());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				count++;
				System.out.println("Done " + company.getSymbol() + ", " + count + " of " + totalSymbols);
			}
		}
	}
	
	
	public void readAllHistoricalEvents() {
		entityManager = JPAHelper.getEntityManager();
		companyBySymbolMap = CompanyUtils.fetchAllCompanyBySymbolMap(entityManager);
		readHistoricalDividendsAndSplits();
	}
	
	
	public static void main(String[] args) {
		YahooDividendStockSplitReader reader = new YahooDividendStockSplitReader();
		reader.readAllHistoricalEvents();
		System.exit(0);
	}
}
