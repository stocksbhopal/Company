package com.sanjoyghosh.company.source.fidelity;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.MyStocks;
import com.sanjoyghosh.company.utils.Constants;

public class FidelityHoldingsDBWriter {
	
//    private static final String ALEXA_USERID = "amzn1.ask.account.AG3AH7ORTENZGSI5ATVRSNF2V4C2QK6CH3IXLPQMPLAWCCTWZNMGGWOGNVG5E6742XCHBILJRV6IIPQHMBLZ6L7TTTZSBVXRDEC567NDTNJHCJBN5P2JXH3C7XEDD7FSHUGIDIOKG7LTDXPZUU7XGF5VXNDMCKUV7CNL7CI7DVAWKANDCHLHWCJDQYS4VITDDBVTOPJ7FSV2MQQ";

	private EntityManager entityManager;
	private Set<Company> companies;
	
	
	public FidelityHoldingsDBWriter() {
		entityManager = JPAHelper.getEntityManager();
		
		companies = new HashSet<>();
		List<Company> companyList = CompanyUtils.fetchCompanyListForAlexaUserId(entityManager, 1);
		for (Company company : companyList) {
			companies.add(company);
		}
	}
	
	
	private File[] getFidelityHoldingsFiles() {
		File[] fidelityFiles = Constants.DownloadsFolder.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().matches(Constants.FidelityHoldingsFileName);
			}
		});
		return fidelityFiles;
	}

	
	private void readFidelityHoldingsFiles(File fidelityFile) {
		Reader reader = null;
		try {
			entityManager.getTransaction().begin();
			reader = new FileReader(fidelityFile);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
			for (CSVRecord record : records) {
				if (record.size() == 14) {
				    String symbol = record.get("Symbol").trim();
				    Company company = CompanyUtils.fetchCompanyBySymbol(entityManager, symbol);
				    if (!companies.contains(company)) {
					    if (company != null) {
					    	System.out.println("Adding Fidelity company: " + company.getName());
					    	MyStocks myStocks = new MyStocks();
					    	myStocks.setAlexaUserId(1);
					    	myStocks.setCompanyId(company.getId());
					    	companies.add(company);
					    	entityManager.persist(myStocks);
					    }
					    else {
					    	System.err.println("No Company for " + symbol);
					    }
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
		
		fidelityFile.delete();
	}
	
	
	public static void main(String[] args) {
		FidelityHoldingsDBWriter reader = new FidelityHoldingsDBWriter();
		File[] fidelityFiles = reader.getFidelityHoldingsFiles();
		for (File fidelityFile : fidelityFiles) {
			try {
				reader.readFidelityHoldingsFiles(fidelityFile);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
	}
}
