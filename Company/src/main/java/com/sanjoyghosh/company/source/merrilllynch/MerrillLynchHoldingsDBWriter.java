package com.sanjoyghosh.company.source.merrilllynch;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.MyStocks;
import com.sanjoyghosh.company.utils.Constants;

public class MerrillLynchHoldingsDBWriter {
	
    private static final String ALEXA_USERID = "amzn1.ask.account.AG3AH7ORTENZGSI5ATVRSNF2V4C2QK6CH3IXLPQMPLAWCCTWZNMGGWOGNVG5E6742XCHBILJRV6IIPQHMBLZ6L7TTTZSBVXRDEC567NDTNJHCJBN5P2JXH3C7XEDD7FSHUGIDIOKG7LTDXPZUU7XGF5VXNDMCKUV7CNL7CI7DVAWKANDCHLHWCJDQYS4VITDDBVTOPJ7FSV2MQQ";
		
	private EntityManager entityManager;
		
	
	public MerrillLynchHoldingsDBWriter() {
		entityManager = JPAHelper.getEntityManager();
	}
	
	
	private File[] getMerrillLynchHoldingsFiles() {
		File[] merrillLynchFiles = Constants.DownloadsFolder.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().matches(Constants.MerrillLynchHoldingsFileName);
			}
		});
		return merrillLynchFiles;
	}
	
	
	private void readMerrillLynchHoldingsFile(File merrillLynchFile) {
		Reader reader = null;
		try {
			entityManager.getTransaction().begin();
			Map<String, Company> companyBySymbolMap = CompanyUtils.fetchAllCompanyBySymbolMap(entityManager);
			Map<Integer, MyStocks> myStocksByCompanyIdMap = CompanyUtils.fetchMyStocksMapByCompanyIdForAlexaUser(entityManager, ALEXA_USERID);
			
			reader = new FileReader(merrillLynchFile);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
			for (CSVRecord record : records) {
				if (record.size() == 16) {
					
				    String symbol = record.get("Symbol").trim();
					Company company = companyBySymbolMap.get(symbol);
				    if (company != null && !myStocksByCompanyIdMap.containsKey(company.getId())) {
				    	System.out.println("Adding Merrill Lynch company: " + company.getName());
				    	MyStocks myStocks = new MyStocks();
				    	myStocks.setAlexaUserId(1);
				    	myStocks.setCompanyId(company.getId());
				    	myStocksByCompanyIdMap.put(company.getId(), myStocks);
				    	entityManager.persist(myStocks);
				    }
				    else if (company == null) {
				    	System.err.println("No Company for " + symbol);
				    }
			    }
			}
			entityManager.getTransaction().commit();
		} 
		catch (IOException e) {
			e.printStackTrace();
			entityManager.getTransaction().rollback();
			return;
		} 
		finally {
			if (reader != null) {
				try {
					reader.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
		
		merrillLynchFile.delete();
	}
		
	
	public static void main(String[] args) {
		MerrillLynchHoldingsDBWriter writer = new MerrillLynchHoldingsDBWriter();
		File[] merrillLynchFiles = writer.getMerrillLynchHoldingsFiles();
		for (File merrillLynchFile : merrillLynchFiles) {
			writer.readMerrillLynchHoldingsFile(merrillLynchFile);
		}
		System.exit(0);
	}
}
