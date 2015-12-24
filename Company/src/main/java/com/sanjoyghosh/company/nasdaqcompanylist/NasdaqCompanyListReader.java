package com.sanjoyghosh.company.nasdaqcompanylist;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.model.Company;
import com.sanjoyghosh.company.model.IndustrySector;

public class NasdaqCompanyListReader {

	private EntityManager entityManager;
	private Map<String, IndustrySector> industrySectorMap = new HashMap<String, IndustrySector>();
	private Map<String, Company> companyMap = new HashMap<String, Company>();
	
	public NasdaqCompanyListReader() {}
	
	public void readCompanyList(Reader reader) throws IOException {
		entityManager = JPAHelper.getEntityManager();
			
		entityManager.getTransaction().begin();
		try {
			fetchAllIndustrySector();
//			fetchAllCompany();
	
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
			for (CSVRecord record : records) {
			    String symbol = record.get("Symbol");
			    String name = record.get("Name");
			    String ipoYearStr = record.get("IPOyear");
			    String sector = record.get("Sector");
			    String industry = record.get("industry");
			    
			    int ipoYear = -1;
			    try {ipoYear = Integer.parseInt(ipoYearStr);}
			    catch (Exception e) {}
	
			    String industrySectorKey = makeIndustrySectorKey(industry, sector);
		    	IndustrySector industrySector = industrySectorMap.get(industrySectorKey);
		    	if (industrySector == null) {
		    		industrySector = new IndustrySector(industry, sector);
		    		entityManager.persist(industrySector);
		    		industrySectorMap.put(industrySectorKey, industrySector);
		    	}
	
		    	Company companyRead = new Company(symbol, name, ipoYear, industrySector.getId());
			    Company company = companyMap.get(symbol);
			    if (company != null) {
			    	if (!company.isIdenticalNonNull(companyRead)) {
			    		entityManager.merge(companyRead);
			    	}
			    }
			    else {
			    	entityManager.persist(companyRead);
			    }
			    System.out.println(symbol + "   " + name);
			}
			entityManager.getTransaction().commit();
		}
		catch (Exception e) {
			e.printStackTrace();
			entityManager.getTransaction().rollback();
		}
	}
	
	@SuppressWarnings("unused")
	private void fetchAllCompany() {
		List<Company> companyList = 
			entityManager.createQuery("SELECT c FROM Company AS c", Company.class)
			.getResultList();
		
		companyMap = new HashMap<String, Company>();
		for (Company company : companyList) {
			companyMap.put(company.getSymbol(), company);
		}
	}
	
	private String makeIndustrySectorKey(String industry, String sector) {
		return industry + "_" + sector;
	}
	
	private void fetchAllIndustrySector() {
		List<IndustrySector> industrySectorList = 
			entityManager.createQuery("SELECT i FROM IndustrySector AS i", IndustrySector.class)
			.getResultList();

		industrySectorMap = new HashMap<String, IndustrySector>();
		for (IndustrySector is : industrySectorList) {
			industrySectorMap.put(makeIndustrySectorKey(is.getIndustry(), is.getSector()), is);
		}
	}
}
