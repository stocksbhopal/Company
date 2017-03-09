package com.sanjoyghosh.company.source.nasdaq;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.persistence.EntityManager;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.CompanyStage;

public class NasdaqCompanyListReader {

	private EntityManager entityManager;
	
	
	public NasdaqCompanyListReader() {}
		
	
	private void readCompanyListFile(File companyListFile, String exchange) throws IOException {
		Reader reader = null;
		try {
			int count = 0;
			reader = new FileReader(companyListFile);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
			for (CSVRecord record : records) {
				String symbol = record.get("Symbol").trim();
				if ((symbol.indexOf('^') >= 0) || (symbol.indexOf('.') >= 0)) {
					continue;
				}

				String sector = record.get("Sector").trim();
				String name = record.get("Name").trim();
				String ipoYearStr = record.get("IPOyear").trim();
				String industry = record.get("industry").trim();
				
				CompanyStage company = new CompanyStage();
				company.setExchange(exchange);
				company.setIndustry(industry);
				company.setIpoYear(ipoYearStr.startsWith("n/a") ? null : Integer.parseInt(ipoYearStr));
				company.setName(name);
				company.setSector(sector);
				company.setSymbol(symbol);
				
				entityManager.persist(company);
				
				count++;
				System.out.println("Done " + symbol + ", " + count + " of " + exchange);
			}
		}
		finally {
			if (reader != null) {
				try {
					reader.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private void readAllCompanyListFiles() {
		entityManager = JPAHelper.getEntityManager();
		
		File nasdaqCompanyListFile = new File("/Users/sanjoyghosh/Downloads/nasdaqcompanylist.csv");
		if (nasdaqCompanyListFile.exists()) {
			entityManager.getTransaction().begin();
			try {
				readCompanyListFile(nasdaqCompanyListFile, "nasdaq");
				entityManager.getTransaction().commit();
			} 
			catch (IOException e) {
				e.printStackTrace();
				entityManager.getTransaction().rollback();
			}
		}

		File nyseCompanyListFile = new File("/Users/sanjoyghosh/Downloads/nysecompanylist.csv");
		if (nyseCompanyListFile.exists()) {
			entityManager.getTransaction().begin();
			try {
				readCompanyListFile(nyseCompanyListFile, "nyse");
				entityManager.getTransaction().commit();
			} 
			catch (IOException e) {
				e.printStackTrace();
				entityManager.getTransaction().rollback();
			}
		}
	}
	
	
	public static void main(String[] args) {
		try {
			NasdaqCompanyListReader reader = new NasdaqCompanyListReader();
			reader.readAllCompanyListFiles();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}
