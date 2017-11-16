package com.sanjoyghosh.company.dynamodb.source.fidelity;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.sanjoyghosh.company.dynamodb.model.Portfolio;


public class FidelityPortfolioDynamoDBReader {	
	
	private String			alexaUserId;
	private File				fidelityFile;
	private LocalDate		addDate;
	
	
	public FidelityPortfolioDynamoDBReader(String alexaUserId, File fidelityFile, LocalDate addDate) {
		this.alexaUserId = alexaUserId;
		this.fidelityFile = fidelityFile;
		this.addDate = addDate;
	}


	public void readFidelityHoldingsFiles(Map<String, Portfolio> portfolioMap) throws IOException {
		Reader reader = null;
		try {
			reader = new FileReader(fidelityFile);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
			for (CSVRecord record : records) {
				if (record.size() == 14) {
					String symbol = record.get("Symbol");
					System.out.println("FIDELITY Symbol: " + symbol);
				    Double quantity = Double.parseDouble(record.get("Quantity").trim());
				    
				    Portfolio portfolio = portfolioMap.get(symbol);
				    if (portfolio != null) {
				    		portfolio.setQuantity(portfolio.getQuantity() + quantity);
				    }
				    else {
					    portfolio = new Portfolio();
					    portfolio.setAlexaUserId(alexaUserId);
					    portfolio.setSymbol(symbol);
					    portfolio.setQuantity(quantity);
					    portfolio.setAddDate(addDate);
					    
					    portfolioMap.put(symbol, portfolio);
				    }
				}
			}
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
