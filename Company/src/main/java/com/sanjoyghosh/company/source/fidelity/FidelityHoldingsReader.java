package com.sanjoyghosh.company.source.fidelity;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.StringUtils;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.Holding;

public class FidelityHoldingsReader {
	
	private EntityManager entityManager;
	
	private Map<String, Company> companyBySymbolMap;
	private HashMap<Holding, Holding> lastHoldingMap = new HashMap<Holding, Holding>();
	private HashSet<Holding> insertHoldingSet = new HashSet<Holding>();
	private HashSet<Holding> updateHoldingSet = new HashSet<Holding>();
	
	
	public FidelityHoldingsReader() {}
	
	
	private void fetchAllFidelityHoldings() {
		entityManager = JPAHelper.getEntityManager();
		companyBySymbolMap = CompanyUtils.fetchAllCompanyBySymbolMap(entityManager);
		List<Holding> currentHoldings = CompanyUtils.fetchAllHoldingAtBrokerage(entityManager, "F");
		for (Holding holding : currentHoldings) {
			lastHoldingMap.put(holding, holding);
		}
	}
	
	
	private File getFidelityHoldingsFile() {
		File downloadsDir = new File("/Users/sanjoyghosh/Downloads");
		File[] fidelityFiles = downloadsDir.listFiles(new FileFilter() {
			
			public boolean accept(File pathname) {
				String fileName = pathname.getName();
				boolean isFidelity = fileName.matches("Portfolio_Position_\\w\\w\\w-\\d\\d-\\d\\d\\d\\d\\.csv");
				return isFidelity;
			}
		});
		
		if (fidelityFiles == null || fidelityFiles.length != 1) {
			System.err.println("There should be exactly 1 Fidelity Holdings file, got " + (fidelityFiles == null ? 0 : fidelityFiles.length));
			return null;
		}
		return fidelityFiles[0];
	}
	
	
	private void readFidelityHoldingsFiles(File fidelityFile) {
		Reader reader = null;
		try {
			reader = new FileReader(fidelityFile);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
			for (CSVRecord record : records) {
				if (record.size() == 14) {
					String account = StringUtils.onlyLast4Characters(record.get("Account Name/Number").trim());
				    String symbol = record.get("Symbol").trim();
				    double quantity = Double.parseDouble(record.get("Quantity").trim());
				    Double lastPrice = StringUtils.toDoubleStringWithDollar(record.get("Last Price").trim());
				    Double boughtPrice = StringUtils.toDoubleStringWithDollar(record.get("Cost Basis Per Share").trim());
	
				    Holding holding = new Holding();
				    holding.setAccount(account);
				    holding.setSymbol(symbol);
				    holding.setQuantity(quantity);
				    holding.setLastPrice(lastPrice);
				    holding.setBoughtPrice(boughtPrice);
				    holding.setBrokerage("F");
				    
				    Holding currentHolding = lastHoldingMap.remove(holding);
				    if (currentHolding == null) {
				    	Company company = companyBySymbolMap.get(holding.getSymbol());
				    	if (company != null) {
				    		holding.setCompanyId(company.getId());
				    	}
				    	insertHoldingSet.add(holding);
				    }
				    else {
				    	updateHoldingSet.add(currentHolding);
				    }
				    System.out.println(holding);
				}
			}
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		finally {
			if (reader != null) {
				try {
					reader.close();
				} 
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private void updateDatabase() {
		entityManager.getTransaction().begin();
		for (Holding holding : insertHoldingSet) {
			entityManager.persist(holding);
		}
		entityManager.getTransaction().commit();
	}
	
	
	public static void main(String[] args) {
		FidelityHoldingsReader reader = new FidelityHoldingsReader();
		reader.fetchAllFidelityHoldings();
		File fidelityFile = reader.getFidelityHoldingsFile();
		if (fidelityFile != null) {
			reader.readFidelityHoldingsFiles(fidelityFile);
			reader.updateDatabase();
		}
		System.exit(0);
	}
}
