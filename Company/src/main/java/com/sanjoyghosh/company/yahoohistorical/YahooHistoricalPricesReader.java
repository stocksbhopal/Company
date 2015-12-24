package com.sanjoyghosh.company.yahoohistorical;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.jsoup.Jsoup;

import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.model.Company;
import com.sanjoyghosh.company.model.IndustrySector;

public class YahooHistoricalPricesReader {

	private EntityManager entityManager;
	
	public YahooHistoricalPricesReader() {}
	
	public void readHistoricalPrices() throws IOException {
		entityManager = JPAHelper.getEntityManager();
		
		List<Company> companyList = CompanyUtils.fetchAllCompany(entityManager);
		for (Company company : companyList) {
			readAllHistoricalPrices(company.getSymbol());
		}
	}

	private void readAllHistoricalPrices(String symbol) {
		String url = "http://real-chart.finance.yahoo.com/table.csv?s=YHOO&a=00&b=01&c=1996&d=11&e=31&f=2015&g=d&ignore=.csv";
		String csvString;
		try {
			csvString = Jsoup.connect(url).execute().body();
			System.out.println(csvString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		YahooHistoricalPricesReader reader = new YahooHistoricalPricesReader();
		reader.readAllHistoricalPrices("YHOO");
		try {
			reader.readHistoricalPrices();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
