package com.sanjoyghosh.company.dynamodb.source.fidelity;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.sanjoyghosh.company.dynamodb.CompanyDynamoDB;
import com.sanjoyghosh.company.dynamodb.helper.CompanyMatcher;
import com.sanjoyghosh.company.dynamodb.model.Portfolio;
import com.sanjoyghosh.company.utils.Constants;


public class FidelityPortfolioDynamoDBReader {	
	
	private String			alexaUserId;
	private String			fidelityFileName;
	private LocalDate		addDate;
	private List<Portfolio>	portfolioList = new ArrayList<>();
	
	
	public FidelityPortfolioDynamoDBReader(String alexaUserId, String fidelityFileName) {
		this.alexaUserId = alexaUserId;
		this.fidelityFileName = fidelityFileName;
		this.addDate = LocalDate.now();
	}


	public void readFidelityHoldingsFiles() throws IOException {
		Reader reader = null;
		try {
			reader = new FileReader(fidelityFileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
			for (CSVRecord record : records) {
				if (record.size() == 14) {
					String symbol = record.get("Symbol");
					System.out.println("FIDELITY Symbol: " + symbol);
				    Double quantity = Double.parseDouble(record.get("Quantity").trim());
				    
				    Portfolio portfolio = new Portfolio();
				    portfolio.setAlexaUserId(alexaUserId);
				    portfolio.setSymbol(symbol);
				    portfolio.setQuantity(quantity);
				    portfolio.setAddDate(addDate);
				    
				    portfolioList.add(portfolio);
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
	

	private void writePortfolioList() {
		DynamoDBMapper mapper = CompanyDynamoDB.getDynamoDBMapper();
					
		long startTime = System.currentTimeMillis();
		System.err.println("Before DynamoDB Portfolio Save");
		List<DynamoDBMapper.FailedBatch> failedList = mapper.batchSave(portfolioList);
		long endTime = System.currentTimeMillis();
		System.err.println("After DynamoDB Portfolio Save: " + (endTime - startTime) + " msecs");
		if (failedList.size() > 0) {
			System.err.println("Failed to BatchSave() Portfolio Records");
			failedList.get(0).getException().printStackTrace();
		}
	}

	
	public static void main(String[] args) {
		String alexaUserId = Constants.MY_ALEXA_USER_ID;
		String fidelityFileName = "/Users/sanjoyg/Downloads/Portfolio_Position_Nov-15-2017.csv";
		FidelityPortfolioDynamoDBReader reader = new FidelityPortfolioDynamoDBReader(alexaUserId, fidelityFileName);
		try {
			reader.readFidelityHoldingsFiles();
			reader.writePortfolioList();
		} 
		catch (Throwable e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}
