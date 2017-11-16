package com.sanjoyghosh.company.dynamodb.batch;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.sanjoyghosh.company.dynamodb.CompanyDynamoDB;
import com.sanjoyghosh.company.dynamodb.model.Portfolio;
import com.sanjoyghosh.company.dynamodb.source.fidelity.FidelityPortfolioDynamoDBReader;
import com.sanjoyghosh.company.dynamodb.source.merrilllynch.MerrillLynchPortfolioDynamoDBReader;
import com.sanjoyghosh.company.utils.Constants;
import com.sanjoyghosh.company.utils.FileUtils;

public class PortfolioUpdater {

	private File				fidelityFile;
	private File				merrillLynchFile;
	Map<String, Portfolio> 	portfolioMap = new HashMap<>();
	
	
	private boolean canBeUpdated() {
		fidelityFile = FileUtils.getLatestFileWithName(Constants.FidelityHoldingsFileName);
		merrillLynchFile = FileUtils.getLatestFileWithName(Constants.MerrillLynchHoldingsFileName);
		return fidelityFile != null && merrillLynchFile != null;
	}
	
	
	private void readPortfolioUpdate() throws IOException {
		LocalDate addDate = LocalDate.now();
		
		FidelityPortfolioDynamoDBReader fidelityReader = 
			new FidelityPortfolioDynamoDBReader(Constants.MY_ALEXA_USER_ID, fidelityFile, addDate);
		fidelityReader.readFidelityHoldingsFiles(portfolioMap);
		
		MerrillLynchPortfolioDynamoDBReader merrillLynchReader = 
			new MerrillLynchPortfolioDynamoDBReader(Constants.MY_ALEXA_USER_ID, merrillLynchFile, addDate);
		merrillLynchReader.readMerrillLynchHoldingsFile(portfolioMap);
	}
	
	
	private void updatePortfolio() throws Exception {
		DynamoDBMapper dynamoDBMapper = CompanyDynamoDB.getDynamoDBMapper();
		long startTime = System.currentTimeMillis();
		long endTime = System.currentTimeMillis();
		
		startTime = System.currentTimeMillis();
		System.err.println("Before DynamoDB Portfolio Old Delete");
		{
			HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
			eav.put(":val1", new AttributeValue().withS(Constants.MY_ALEXA_USER_ID));
			DynamoDBQueryExpression<Portfolio> queryExpression = new DynamoDBQueryExpression<Portfolio>()
				.withKeyConditionExpression("AlexaUserId = :val1")
				.withExpressionAttributeValues(eav);
			List<Portfolio> oldPortfolioList = dynamoDBMapper.query(Portfolio.class, queryExpression);
			dynamoDBMapper.batchDelete(oldPortfolioList);
		}
		endTime = System.currentTimeMillis();
		System.err.println("After DynamoDB Portfolio Old Delete: " + (endTime - startTime) + " msecs");
		
		startTime = System.currentTimeMillis();
		System.err.println("Before DynamoDB Portfolio Save");
		{
			
			List<DynamoDBMapper.FailedBatch> failedList = dynamoDBMapper.batchSave(portfolioMap.values());
			if (failedList.size() > 0) {
				System.err.println("Failed to batchSave() Portfolio Records");
				throw failedList.get(0).getException();
			}
		}
		endTime = System.currentTimeMillis();
		System.err.println("After DynamoDB Portfolio Save: " + (endTime - startTime) + " msecs");
	}
	
	
	private void deleteFiles() {
		fidelityFile.delete();
		merrillLynchFile.delete();
	}
	
	
	public static void main(String[] args) {
		PortfolioUpdater updater = new PortfolioUpdater();
		try {
			if (updater.canBeUpdated()) {
				updater.readPortfolioUpdate();
				updater.updatePortfolio();
				updater.deleteFiles();
			}
			else {
				System.err.println("Either Fidelity or Merrill Lynch files missing");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}
}
