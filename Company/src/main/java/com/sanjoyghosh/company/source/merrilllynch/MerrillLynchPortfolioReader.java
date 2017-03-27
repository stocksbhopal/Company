package com.sanjoyghosh.company.source.merrilllynch;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.persistence.EntityManager;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.sanjoyghosh.company.db.PortfolioJPA;
import com.sanjoyghosh.company.db.model.Portfolio;
import com.sanjoyghosh.company.utils.Constants;

public class MerrillLynchPortfolioReader {
		
	private Portfolio portfolio;
	
	
	public MerrillLynchPortfolioReader(Portfolio portfolio) {
		this.portfolio = portfolio;
	}
	
	
	public File[] getMerrillLynchHoldingsFiles() {
		File[] merrillLynchFiles = Constants.DownloadsFolder.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().matches(Constants.MerrillLynchHoldingsFileName);
			}
		});
		return merrillLynchFiles;
	}
	
	
	public void readMerrillLynchHoldingsFile(EntityManager em, File merrillLynchFile) {
		Reader reader = null;
		try {
			reader = new FileReader(merrillLynchFile);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
			for (CSVRecord record : records) {
				if (record.size() == 16) {
				    String symbol = record.get("Symbol").trim();
					System.out.println("MERRILL Symbol: " + symbol);
				    Double quantity = Double.parseDouble(record.get("Quantity").replaceAll(",", "").trim());
				    PortfolioJPA.makePortfolioItem(em, portfolio, symbol, quantity);
				}
			}
		} 
		catch (IOException e) {
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
	}
}
