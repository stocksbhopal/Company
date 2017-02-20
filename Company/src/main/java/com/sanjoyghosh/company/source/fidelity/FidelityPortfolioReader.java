package com.sanjoyghosh.company.source.fidelity;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.sanjoyghosh.company.db.PortfolioJPA;
import com.sanjoyghosh.company.db.model.Portfolio;
import com.sanjoyghosh.company.utils.Constants;

public class FidelityPortfolioReader {	
	
	private Portfolio portfolio;
	
	
	public FidelityPortfolioReader(Portfolio portfolio) {
		this.portfolio = portfolio;
	}


	public File[] getFidelityHoldingsFiles() {
		File[] fidelityFiles = Constants.DownloadsFolder.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().matches(Constants.FidelityHoldingsFileName);
			}
		});
		return fidelityFiles;
	}

	
	public void readFidelityHoldingsFiles(File fidelityFile) {
		Reader reader = null;
		try {
			reader = new FileReader(fidelityFile);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
			for (CSVRecord record : records) {
				if (record.size() == 14) {
					String symbol = record.get("Symbol");
					System.out.println("FIDELITY Symbol: " + symbol);
				    Double quantity = Double.parseDouble(record.get("Quantity").trim());
				    PortfolioJPA.makePortfolioItem(portfolio, symbol, quantity);
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
