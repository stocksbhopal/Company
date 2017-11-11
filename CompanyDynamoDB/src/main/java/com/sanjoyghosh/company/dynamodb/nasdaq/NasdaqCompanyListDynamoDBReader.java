package com.sanjoyghosh.company.dynamodb.nasdaq;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.sanjoyghosh.company.dynamodb.model.Company;
import com.sanjoyghosh.company.dynamodb.model.CompanyNameMatch;

public class NasdaqCompanyListDynamoDBReader {

	public NasdaqCompanyListDynamoDBReader() {}
		
	
	private void readCompanyListFile(String fileName, String exchange, List<Company> companyList, Set<String> companyNames) {
		Reader reader = null;
		try {
			reader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(fileName));
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
			for (CSVRecord record : records) {
				String symbol = record.get("Symbol").trim();
				String name = record.get("Name").trim();
				String nameStripped = CompanyNameMatcher.stripStopWordsFromName(name);

				if ((symbol.indexOf('^') >= 0) || (symbol.indexOf('.') >= 0)) {
					System.err.println("DISCARD_symbol: " + exchange + ": " + symbol + ": " + nameStripped + ": " + name);
					continue;
				}

				// To drop entries like Wells Fargo Advantage Funds - Wells Fargo Global Dividend Opportunity Fund (EOD).
				if (name.endsWith(" Fund") || name.indexOf(" Fund ") >= 0 || name.indexOf(" Funds, ") >= 0) {
					System.err.println("DISCARD_fund: " + exchange + ": " + symbol + ": " + nameStripped + ": " + name);
					continue;
				}
				if (name.endsWith(" ETF")) {
					System.err.println("DISCARD_etf: " + exchange + ": " + symbol + ": " + nameStripped + ": " + name);
					continue;
				}
				
				// This is to exclude derivative stocks for the same company.
				// The first entry in the spreadsheet is the main company.
				// Ignore the following ones.
				if (companyNames.contains(name)) {
					System.err.println("DISCARD_repeat: " + ": " + symbol + ": " + nameStripped + ": " + name);
					continue;
				}
				
				companyNames.add(name);

				String sector = record.get("Sector").trim();
				String ipoYearStr = record.get("IPOyear").trim();
				String industry = record.get("industry").trim();
				
				Company company = new Company();
				company.setExchange(exchange);
				company.setIndustry(industry);
				company.setIpoYear(ipoYearStr.startsWith("n/a") ? null : Integer.parseInt(ipoYearStr));
				company.setName(name);
				company.setNameStripped(nameStripped);
				company.setSector(sector);
				company.setSymbol(symbol);
				companyList.add(company);
				
				CompanyNameMatcher.processStrippedName(nameStripped, company);
				System.out.println(symbol + ": " + nameStripped + ": " + name);
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (reader != null) {
				try {
					reader.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public static void main(String[] args) {
		AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.standard()
			.withCredentials(new ProfileCredentialsProvider())
			.withRegion(Regions.US_EAST_1).build();
		DynamoDBMapper mapper = new DynamoDBMapper(dynamoDB);
		
		List<Company> companyList = new ArrayList<Company>();
		Set<String> companyNames = new HashSet<>();
		List<CompanyNameMatch> companyNameMatchList = new ArrayList<>();
		
		try {
			NasdaqCompanyListDynamoDBReader reader = new NasdaqCompanyListDynamoDBReader();
			
			reader.readCompanyListFile("nasdaqcompanylist.csv", "nasdaq", companyList, companyNames);		
			reader.readCompanyListFile("nysecompanylist.csv", "nyse", companyList, companyNames);
			CompanyNameMatcher.assignNameToCompany(companyNameMatchList);

			long startTime = System.currentTimeMillis();
			System.err.println("Before DynamoDB Company Save");
			List<DynamoDBMapper.FailedBatch> failedList = mapper.batchSave(companyList);
			long endTime = System.currentTimeMillis();
			System.err.println("After DynamoDB Company Save: " + (endTime - startTime) + " msecs");
			if (failedList.size() > 0) {
				System.err.println("Failed to BatchSave() Company Records");
				failedList.get(0).getException().printStackTrace();
			}

			startTime = System.currentTimeMillis();
			System.err.println("Before DynamoDB CompanyNameMatch Save");
			failedList = mapper.batchSave(companyNameMatchList);
			endTime = System.currentTimeMillis();
			System.err.println("After DynamoDB CompanyNameMatch Save: " + (endTime - startTime) + " msecs");
			if (failedList.size() > 0) {
				System.err.println("Failed to BatchSave() CompanyNameMatch Records");
				failedList.get(0).getException().printStackTrace();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		System.exit(0);
	}
}
