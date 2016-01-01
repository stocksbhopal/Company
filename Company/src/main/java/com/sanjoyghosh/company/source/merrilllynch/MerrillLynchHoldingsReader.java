package com.sanjoyghosh.company.source.merrilllynch;

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

public class MerrillLynchHoldingsReader {
	
	private EntityManager entityManager;
	
	private Map<String, Company> companyBySymbolMap;
	private HashMap<Holding, Holding> lastHoldingMap = new HashMap<Holding, Holding>();
	private HashSet<Holding> insertHoldingSet = new HashSet<Holding>();
	private HashSet<Holding> updateHoldingSet = new HashSet<Holding>();
	
	
	public MerrillLynchHoldingsReader() {}
	
	
	private void fetchAllMerrillLynchHoldings() {
		entityManager = JPAHelper.getEntityManager();
		companyBySymbolMap = CompanyUtils.fetchAllCompanyBySymbolMap(entityManager);
		List<Holding> currentHoldings = CompanyUtils.fetchAllHoldingAtBrokerage(entityManager, "L");
		for (Holding holding : currentHoldings) {
			lastHoldingMap.put(holding, holding);
		}
	}
	
	
	private File getMerrillLynchHoldingsFile() {
		File downloadsDir = new File("/Users/sanjoyghosh/Downloads");
		File[] merrillLynchFiles = downloadsDir.listFiles(new FileFilter() {
			
			public boolean accept(File pathname) {
				String fileName = pathname.getName();
				boolean isMerrillLynch = fileName.matches("Holdings_\\d\\d\\d\\d\\d\\d\\d\\d\\.csv");
				return isMerrillLynch;
			}
		});
		
		if (merrillLynchFiles == null || merrillLynchFiles.length != 1) {
			System.err.println("There should be exactly 1 Merrill Lynch Holdings file, got " + (merrillLynchFiles == null ? 0 : merrillLynchFiles.length));
			return null;
		}
		return merrillLynchFiles[0];
	}
	
	
	private void readMerrillLynchHoldingsFiles(File fidelityFile) {
		Reader reader = null;
		try {
			reader = new FileReader(fidelityFile);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
			for (CSVRecord record : records) {
				if (record.size() == 16) {
					String account = StringUtils.onlyLast4Characters(record.get("Account #").trim());
				    String symbol = record.get("Symbol").trim();
				    double quantity = Double.parseDouble(record.get("Quantity").replaceAll(",", "").trim());
				    Double lastPrice = Double.parseDouble(record.get("Price ($)").replaceAll(",", "").trim());
	
				    Holding holding = new Holding();
				    holding.setAccount(account);
				    holding.setSymbol(symbol);
				    holding.setQuantity(quantity);
				    holding.setLastPrice(lastPrice);
				    holding.setBrokerage("L");
				    
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
		MerrillLynchHoldingsReader reader = new MerrillLynchHoldingsReader();
		reader.fetchAllMerrillLynchHoldings();
		File merrillLynchFile = reader.getMerrillLynchHoldingsFile();
		if (merrillLynchFile != null) {
			reader.readMerrillLynchHoldingsFiles(merrillLynchFile);
			reader.updateDatabase();
		}
		System.exit(0);
	}
}
