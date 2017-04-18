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

	public NasdaqCompanyListReader() {}
		
	
	private void readCompanyListFile(File companyListFile, String exchange, EntityManager entityManager) throws IOException {
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
				String industry = record.get("Industry").trim();
				// To drop entries like Wells Fargo Advantage Funds - Wells Fargo Global Dividend Opportunity Fund (EOD).
				if (name.endsWith(" Fund") || name.indexOf(" Fund ") >= 0 || name.indexOf(" Funds, ") >= 0) {
					continue;
				}
				
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
	
	
	private void readAllCompanyListFiles(EntityManager entityManager) {
		File nasdaqCompanyListFile = new File("/Users/sanjoyghosh/Downloads/nasdaqcompanylist.csv");
		if (nasdaqCompanyListFile.exists()) {
			entityManager.getTransaction().begin();
			try {
				entityManager.createQuery("DELETE FROM CompanyStage WHERE exchange = 'nasdaq'").executeUpdate();
				readCompanyListFile(nasdaqCompanyListFile, "nasdaq", entityManager);
				entityManager.getTransaction().commit();
			} 
			catch (Exception e) {
				e.printStackTrace();
				if (entityManager.getTransaction().isActive()) {
					entityManager.getTransaction().rollback();
				}
			}
		}

		File nyseCompanyListFile = new File("/Users/sanjoyghosh/Downloads/nysecompanylist.csv");
		if (nyseCompanyListFile.exists()) {
			entityManager.getTransaction().begin();
			try {
				entityManager.createQuery("DELETE FROM CompanyStage WHERE exchange = 'nyse'").executeUpdate();
				readCompanyListFile(nyseCompanyListFile, "nyse", entityManager);
				entityManager.getTransaction().commit();
			} 
			catch (Exception e) {
				e.printStackTrace();
				if (entityManager.getTransaction().isActive()) {
					entityManager.getTransaction().rollback();
				}
			}
		}
	}
	
	
	public static void main(String[] args) {
		EntityManager entityManager = null;
		for (String mySQLHost : JPAHelper.getMySQLHostList()) {
			try {
				entityManager = JPAHelper.getEntityManager(mySQLHost);
				NasdaqCompanyListReader reader = new NasdaqCompanyListReader();
				reader.readAllCompanyListFiles(entityManager);		
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
