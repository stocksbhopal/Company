package com.sanjoyghosh.company.dynamodb.source.merrilllynch;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.sanjoyghosh.company.dynamodb.model.Portfolio;

public class MerrillLynchPortfolioDynamoDBReader {
		
	private String			alexaUserId;
	private File				merrillLynchFile;
	private LocalDate		addDate;
	
	
	public MerrillLynchPortfolioDynamoDBReader(String alexaUserId, File merrillLynchFile, LocalDate addDate) {
		this.alexaUserId = alexaUserId;
		this.merrillLynchFile = merrillLynchFile;
		this.addDate = addDate;
	}
	

	public void readMerrillLynchHoldingsFile(Map<String, Portfolio> portfolioMap) throws IOException {
		Reader reader = null;
		try {
			reader = new FileReader(merrillLynchFile);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
			for (CSVRecord record : records) {
				if (record.size() == 16) {
				    String symbol = record.get("Symbol").trim();
					System.out.println("MERRILL Symbol: " + symbol);
				    Double quantity = Double.parseDouble(record.get("Quantity").replaceAll(",", "").trim());
				    
				    if (symbol.equals("--")) {
				    		// This is just ML Bank deposits.
				    		continue;
				    }
				    
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
