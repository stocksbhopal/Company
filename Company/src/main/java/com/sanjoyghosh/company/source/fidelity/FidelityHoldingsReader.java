package com.sanjoyghosh.company.source.fidelity;

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

import com.sanjoyghosh.company.aws.Constants;
import com.sanjoyghosh.company.aws.FileDateUtils;
import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.Holding;
import com.sanjoyghosh.company.utils.StringUtils;

public class FidelityHoldingsReader {
	
	private EntityManager entityManager;	
	
	
	public FidelityHoldingsReader() {
		entityManager = JPAHelper.getEntityManager();
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
		entityManager.getTransaction().begin();
		
		Reader reader = null;
		try {
			String fileName = fidelityFile.getName();
			Date cobDate = FileDateUtils.getDateFromFidelityHoldingsFileName(fileName);
			int numHoldingsDeleted = CompanyUtils.deleteAllHoldingsByDateBrokerage(entityManager, Constants.FidelityBrokerage, cobDate);
			System.out.println("Deleted " + numHoldingsDeleted + " for Fidelity on: " + DateFormat.getDateInstance().format(cobDate));

			reader = new FileReader(fidelityFile);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
			for (CSVRecord record : records) {
				if (record.size() == 14) {
					String account = StringUtils.onlyLast4Characters(record.get("Account Name/Number").trim());
				    String symbol = record.get("Symbol").trim();
				    Company company = CompanyUtils.fetchCompanyBySymbol(entityManager, symbol);
				    Double quantity = Double.parseDouble(record.get("Quantity").trim());
				    Double cobPrice = StringUtils.toDoubleStringWithDollar(record.get("Last Price").trim());
				    Double boughtPrice = StringUtils.toDoubleStringWithDollar(record.get("Cost Basis Per Share").trim());
				    Double value = StringUtils.toDoubleStringWithDollar(record.get("Current Value").replaceAll(",", "").trim());
				    Double gain = StringUtils.parseDoubleWithSignAndDollar(record.get("Total Gain/Loss Dollar").replaceAll(",", "").trim());
				    Double gainPercent = StringUtils.parseDoubleWithSignAndPercent(record.get("Total Gain/Loss Percent").replaceAll(",", "").trim());
	
				    Holding holding = new Holding();
				    holding.setCompanyId(company == null ? 0 : company.getId());
				    holding.setBoughtPrice(boughtPrice);
				    holding.setBoughtDate(null);
				    holding.setCobPrice(cobPrice);
				    holding.setCobDate(new Timestamp(cobDate.getTime()));
				    holding.setQuantity(quantity);
				    holding.setBrokerage(Constants.FidelityBrokerage);
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
		fidelityFile.delete();
	}
	
	
	public static void main(String[] args) {
		FidelityHoldingsReader reader = new FidelityHoldingsReader();
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
