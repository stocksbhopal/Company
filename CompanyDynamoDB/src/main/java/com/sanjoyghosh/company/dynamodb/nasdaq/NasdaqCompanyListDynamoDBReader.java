package com.sanjoyghosh.company.dynamodb.nasdaq;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.sanjoyghosh.company.dynamodb.model.CompanyDynamoDB;

public class NasdaqCompanyListDynamoDBReader {

	public NasdaqCompanyListDynamoDBReader() {}
		
	
	private void readCompanyListFile(String fileName, String exchange, DynamoDBMapper mapper) {
		Reader reader = null;
		try {
			int count = 0;
			List<CompanyDynamoDB> companyList = new ArrayList<CompanyDynamoDB>();
			reader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(fileName));
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
			for (CSVRecord record : records) {

				String symbol = record.get("Symbol").trim();
				if ((symbol.indexOf('^') >= 0) || (symbol.indexOf('.') >= 0)) {
					continue;
				}

				String name = record.get("Name").trim();
				// To drop entries like Wells Fargo Advantage Funds - Wells Fargo Global Dividend Opportunity Fund (EOD).
				if (name.endsWith(" Fund") || name.indexOf(" Fund ") >= 0 || name.indexOf(" Funds, ") >= 0) {
					continue;
				}

				String sector = record.get("Sector").trim();
				String ipoYearStr = record.get("IPOyear").trim();
				String industry = record.get("industry").trim();
				
				CompanyDynamoDB company = new CompanyDynamoDB();
				company.setExchange(exchange);
				company.setIndustry(industry);
				company.setIpoYear(ipoYearStr.startsWith("n/a") ? null : Integer.parseInt(ipoYearStr));
				company.setName(name);
				company.setSector(sector);
				company.setSymbol(symbol);
				companyList.add(company);
				
				if (companyList.size() % 10000 == 0) {
					List<DynamoDBMapper.FailedBatch> failedList = mapper.batchSave(companyList);
					if (failedList.size() > 0) {
						System.err.println("Failed to BatchSave() Company Records");
						failedList.get(0).getException().printStackTrace();
						System.exit(-1);
					}
					companyList.clear();
				}
				
				count++;
				System.out.println("Done " + symbol + ", " + count + " of " + exchange);
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
		
		try {
			NasdaqCompanyListDynamoDBReader reader = new NasdaqCompanyListDynamoDBReader();
			reader.readCompanyListFile("nasdaqcompanylist.csv", "nasdaq", mapper);		
			reader.readCompanyListFile("nysecompanylist.csv", "nyse", mapper);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		System.exit(0);
	}
}
