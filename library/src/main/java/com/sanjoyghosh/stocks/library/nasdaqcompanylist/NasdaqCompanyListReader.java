package com.sanjoyghosh.stocks.library.nasdaqcompanylist;

import java.io.IOException;
import java.io.Reader;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.sanjoyghosh.stocks.library.db.JPAHelper;
import com.sanjoyghosh.stocks.library.model.Company;
import com.sanjoyghosh.stocks.library.model.IndustrySector;

public class NasdaqCompanyListReader {

	private EntityManager entityManager;
	
	public NasdaqCompanyListReader() {
		JPAHelper.createEntityManager();
		entityManager = JPAHelper.getEntityManager();
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
//		    System.out.println(symbol + "  " + name + "  " + ipoYear);
		    
		    if (entityManager != null) {
			    Company company = fetchCompany(symbol);
			    if (company == null) {
				    IndustrySector industrySector = fetchIndustrySector(industry, sector);
				    if (industrySector == null) {
				    	industrySector = storeIndustrySector(industry, sector);
				    }
				    if (industrySector != null) {
				    	company = storeCompany(symbol, name, ipoYear, industrySector.getId());
				    }
			    }
		    }
		}
	}
	
	private Company fetchCompany(String symbol) {
		try {
			Company fetchedCompany =
				entityManager.createQuery("SELECT c FROM Company AS c WHERE symbol = :symbol", Company.class)
				.setParameter("symbol", symbol)
				.getSingleResult();
			return fetchedCompany;
		}
		catch (NoResultException e) {}
		return null;
	}
	
	private Company storeCompany(String symbol, String name, int ipoYear, int industrySectorId) {
		try {
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
		catch (Exception e) {
			e.printStackTrace();
			entityManager.getTransaction().rollback();
			return null;
		}
	}
	
	private IndustrySector fetchIndustrySector(String industry, String sector) {
		try {
			IndustrySector fetchedIndustrySector = 
				entityManager.createQuery("SELECT i FROM IndustrySector AS i WHERE industry = :industry AND sector = :sector", IndustrySector.class)
				.setParameter("industry", industry)
				.setParameter("sector", sector)
				.getSingleResult();
			return fetchedIndustrySector;
		}
		catch (NoResultException e) {}
		return null;
	}
	
	private IndustrySector storeIndustrySector(String industry, String sector) {
		try {
			entityManager.getTransaction().begin();
			IndustrySector storedIndustrySector = new IndustrySector();
			storedIndustrySector.setIndustry(industry);
			storedIndustrySector.setSector(sector);
			entityManager.persist(storedIndustrySector);
			entityManager.getTransaction().commit();
			return storedIndustrySector;
		}
		catch (Exception e) {
			e.printStackTrace();
			entityManager.getTransaction().rollback();
			return null;
		}
	}
}
