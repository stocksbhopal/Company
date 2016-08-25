package com.sanjoyghosh.company.source.merrilllynch;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.persistence.EntityManager;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.Holding;
import com.sanjoyghosh.company.utils.Constants;
import com.sanjoyghosh.company.utils.DateUtils;
import com.sanjoyghosh.company.utils.StringUtils;

public class MerrillLynchHoldingsReader {
		
	private EntityManager entityManager;
		
	
	public MerrillLynchHoldingsReader() {
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
		entityManager.getTransaction().begin();

		Reader reader = null;
		try {
			String fileName = merrillLynchFile.getName();
			Date cobDate = DateUtils.getDateFromMerrillLynchHoldingsFileName(fileName);
			int numHoldingsDeleted = CompanyUtils.deleteAllHoldingsByDateBrokerage(entityManager, Constants.MerrillLynchBrokerage, cobDate);
			System.out.println("Deleted " + numHoldingsDeleted + " for Merrill Lynch on: " + DateFormat.getDateInstance().format(cobDate));
			
			reader = new FileReader(merrillLynchFile);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
			for (CSVRecord record : records) {
				if (record.size() == 16) {
					String account = StringUtils.onlyLast4Characters(record.get("Account #").trim());
				    String symbol = record.get("Symbol").trim();
				    Double quantity = Double.parseDouble(record.get("Quantity").replaceAll(",", "").trim());
				    Double cobPrice = Double.parseDouble(record.get("Price ($)").replaceAll(",", "").trim());
				    Company company = CompanyUtils.fetchCompanyBySymbol(entityManager, symbol);
				    Double value = StringUtils.parseDoubleWithBrackets(record.get("Value ($)").replaceAll(",", "").trim());
				    Double gain = StringUtils.parseDoubleWithBrackets(record.get("Unrealized Gain/Loss ($)").replaceAll(",", "").trim());
				    Double gainPercent = StringUtils.parseDoubleWithBrackets(record.get("Unrealized Gain/Loss (%)").replaceAll(",", "").trim());
	
				    Holding holding = new Holding();
				    holding.setCompanyId(company == null ? 0 : company.getId());
				    holding.setBoughtPrice(null);
				    holding.setBoughtDate(null);
				    holding.setCobPrice(cobPrice);
				    holding.setCobDate(new Timestamp(cobDate.getTime()));
				    holding.setQuantity(quantity);
				    holding.setBrokerage(Constants.MerrillLynchBrokerage);
				    holding.setSymbol(symbol);
				    holding.setAccount(account);
				    holding.setValue(value);
				    holding.setGain(gain);
				    holding.setGainPercent(gainPercent);
				    
				    entityManager.persist(holding);
				    System.out.println(holding);
				}
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (ParseException e) {
			e.printStackTrace();
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
		
		entityManager.getTransaction().commit();
		merrillLynchFile.delete();
	}
		
	
	public static void main(String[] args) {
		MerrillLynchHoldingsReader reader = new MerrillLynchHoldingsReader();
		File[] merrillLynchFiles = reader.getMerrillLynchHoldingsFiles();
		for (File merrillLynchFile : merrillLynchFiles) {
			reader.readMerrillLynchHoldingsFile(merrillLynchFile);
		}
		System.exit(0);
	}
}
