package com.sanjoyghosh.stocks.library.nasdaqcompanylist;

import java.io.IOException;
import java.io.Reader;

import javax.persistence.EntityManager;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.sanjoyghosh.stocks.library.model.Company;
import com.sanjoyghosh.stocks.library.model.IndustrySector;

public class NasdaqCompanyListReader {

	private EntityManager entityManager;
	
	public NasdaqCompanyListReader(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	public void readCompanyList(Reader reader) throws IOException {
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
		for (CSVRecord record : records) {
		    String symbol = record.get("Symbol");
		    String name = record.get("Name");
		    String ipoYearStr = record.get("IPOyear");
		    String sector = record.get("Sector");
		    String industry = record.get("industry");
		    System.out.println(symbol + "   " + name);
		    
		    int ipoYear = -1;
		    try {
		    	ipoYear = Integer.parseInt(ipoYearStr);
		    }
		    catch (Exception e) { // Not a valid ipo year.
		    }
		    
		    Company company = fetchCompany(symbol);
		    if (company == null) {
			    IndustrySector industrySector = fetchIndustrySector(industry, sector);
			    if (industrySector == null) {
			    	industrySector = storeIndustrySector(industry, sector);
			    }
			    company = storeCompany(symbol, name, ipoYear, industrySector.getId());
		    }
		}
	}
	
	private Company fetchCompany(String symbol) {
		Company fetchedCompany =
			entityManager.createQuery("SELECT c FROM Company WHERE symbol = :symbol", Company.class)
			.setParameter("symbol", symbol)
			.getSingleResult();
		return fetchedCompany;
	}
	
	private Company storeCompany(String symbol, String name, int ipoYear, int industrySectorId) {
		entityManager.getTransaction().begin();
		Company company = new Company();
		company.setSymbol(symbol);
		company.setName(name);
		company.setIpoYear(ipoYear);
		company.setIndustrySectorId(industrySectorId);
		entityManager.persist(company);
		entityManager.getTransaction().commit();
		return company;
	}
	
	private IndustrySector fetchIndustrySector(String industry, String sector) {
		IndustrySector fetchedIndustrySector = 
			entityManager.createQuery("SELECT is FROM IndustrySector WHERE industry = :industry AND sector = :sector", IndustrySector.class)
			.setParameter("industry", industry)
			.setParameter("sector", sector)
			.getSingleResult();
		return fetchedIndustrySector;
	}
	
	private IndustrySector storeIndustrySector(String industry, String sector) {
		entityManager.getTransaction().begin();
		IndustrySector storedIndustrySector = new IndustrySector();
		storedIndustrySector.setIndustry(industry);
		storedIndustrySector.setSector(sector);
		entityManager.persist(storedIndustrySector);
		entityManager.getTransaction().commit();
		return storedIndustrySector;
	}
}
