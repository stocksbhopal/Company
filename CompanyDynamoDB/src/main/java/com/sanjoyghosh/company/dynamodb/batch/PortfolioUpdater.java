package com.sanjoyghosh.company.dynamodb.batch;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sanjoyghosh.company.dynamodb.CompanyDynamoDB;
import com.sanjoyghosh.company.dynamodb.helper.PortfolioMatcher;
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
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void updatePortfolio() throws Exception {
		List<Portfolio> oldPortfolioList = PortfolioMatcher.getPortfolioForAlexaUser(Constants.MY_ALEXA_USER_ID);
		CompanyDynamoDB.batchDeleteDynamoDB((Iterable) oldPortfolioList, "Portfolio");
		CompanyDynamoDB.batchSaveDynamoDB(portfolioMap.values(), "Portfolio");
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
