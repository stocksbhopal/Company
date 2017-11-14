package com.sanjoyghosh.company.dynamodb.nasdaq;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.sanjoyghosh.company.dynamodb.CompanyDynamoDB;
import com.sanjoyghosh.company.dynamodb.helper.CompanyNameMatcher;
import com.sanjoyghosh.company.dynamodb.model.Company;
import com.sanjoyghosh.company.dynamodb.model.CompanyName;

public class NasdaqCompanyListDynamoDBReader {

	public NasdaqCompanyListDynamoDBReader() {}
		
	
	private List<Company>			companyList = new ArrayList<Company>();
	private Set<String>				companyNames = new HashSet<>();
	private Map<String, Company>		companyBySymbolMap = new HashMap<>();
	private List<CompanyName>	companyNameList = new ArrayList<>();

	
	private void readCompanyListFile(String fileName, String exchange) throws IOException {
		Reader reader = null;
		try {
			reader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(fileName));
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
			for (CSVRecord record : records) {
				String symbol = record.get("Symbol").trim();
				String name = record.get("Name").trim();
				String nameStripped = CompanyNameMatcher.stripStopWordsFromName(name);

				if ((symbol.indexOf("^") >= 0) || (symbol.indexOf(".") >= 0)) {
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
				companyNames.add(name);
				companyBySymbolMap.put(symbol, company);
				
				CompanyNameMatcher.processStrippedName(company.getNameStripped(), company);
				System.out.println(symbol + ": " + nameStripped + ": " + name);
			}
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
	
	
	private void addTheStockIndexes() {
		Company company = new Company();
		company.setName("Dow Jones Industrial Average");
		company.setNameStripped(CompanyNameMatcher.stripStopWordsFromName(company.getName()));
		company.setSymbol("DJIA");
		
		companyList.add(company);
		companyBySymbolMap.put(company.getSymbol(), company);
		CompanyNameMatcher.processStrippedName(company.getNameStripped(), company);

		company = new Company();
		company.setName("NASDAQ Composite");
		company.setNameStripped(CompanyNameMatcher.stripStopWordsFromName(company.getName()));
		company.setSymbol("IXIC");
		
		companyList.add(company);
		companyBySymbolMap.put(company.getSymbol(), company);
		CompanyNameMatcher.processStrippedName(company.getNameStripped(), company);

		company = new Company();
		company.setName("S&P 500");
		company.setNameStripped(CompanyNameMatcher.stripStopWordsFromName(company.getName()));
		company.setSymbol("GSPC");
		
		companyList.add(company);
		companyBySymbolMap.put(company.getSymbol(), company);
		CompanyNameMatcher.processStrippedName(company.getNameStripped(), company);
	}
	
	
	private void addPopularCompanyName(String symbol, String name) {
		Company company = companyBySymbolMap.get(symbol);
		if (company == null) {
			System.err.println("No Company found for symbol: " + symbol);
			return;
		}
		
		CompanyNameMatcher.processStrippedName(name, company);
	}
	
	
	private void addPopularCompanyNames() {
		addPopularCompanyName("DJIA", "the dow");
		addPopularCompanyName("DJIA", "the dow jones");
		addPopularCompanyName("DJIA", "the dow jones industrial average");
		addPopularCompanyName("DJIA", "dow jones");
		addPopularCompanyName("DJIA", "dow jones industrial average");
		addPopularCompanyName("IXIC", "the nasdaq");
		addPopularCompanyName("IXIC", "the nasdaq composite");
		addPopularCompanyName("IXIC", "nasdaq");
		addPopularCompanyName("IXIC", "nasdaq composite");
		addPopularCompanyName("GSPC", "the s and p");
		addPopularCompanyName("GSPC", "the s and p 500");
		addPopularCompanyName("GSPC", "s and p");
		addPopularCompanyName("GSPC", "s and p 500");
		addPopularCompanyName("HAL", "haliburton");
		addPopularCompanyName("NOW", "service now");
		addPopularCompanyName("X", "us steel");
		addPopularCompanyName("UA", "under armor");
		addPopularCompanyName("PLCE", "children\"s");
		addPopularCompanyName("BKU", "bank united");
		addPopularCompanyName("EPD", "enterprise lp");
		addPopularCompanyName("CRM", "sales force");
		addPopularCompanyName("CHKP", "checkpoint");
		addPopularCompanyName("PCLN", "price line");
		addPopularCompanyName("GDDY", "go daddy");
		addPopularCompanyName("GDDY", "gold daddy");
		addPopularCompanyName("SNPS", "synopsis");
		addPopularCompanyName("T", "at and t");
		addPopularCompanyName("T", "tea");
		addPopularCompanyName("FL", "footlocker");
		addPopularCompanyName("JNJ", "johnson and johnson");
		addPopularCompanyName("L", "lose");
		addPopularCompanyName("L", "loaves");
		addPopularCompanyName("AMZN", "amazon");
		addPopularCompanyName("GOOG", "google");
		addPopularCompanyName("HON", "honey well");
		addPopularCompanyName("SWKS", "sky works");
		addPopularCompanyName("MBLY", "mobile i");
		addPopularCompanyName("C", "city group");
		addPopularCompanyName("C", "citi bank");
		addPopularCompanyName("COR", "core sight");
		addPopularCompanyName("DIS", "disney");		
	}
	
	
	private void processAllCompanies() throws IOException {
		readCompanyListFile("nasdaqcompanylist.csv", "nasdaq");		
		readCompanyListFile("nysecompanylist.csv", "nyse");
		addTheStockIndexes();
		addPopularCompanyNames();
		
		CompanyNameMatcher.assignNamePrefixToCompany(companyNameList);
	}
	
	
	private void writeAllCompanies() {
		DynamoDBMapper mapper = CompanyDynamoDB.getDynamoDBMapper();
					
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
		System.err.println("Before DynamoDB CompanyName Save");
		failedList = mapper.batchSave(companyNameList);
		endTime = System.currentTimeMillis();
		System.err.println("After DynamoDB CompanyName Save: " + (endTime - startTime) + " msecs");
		if (failedList.size() > 0) {
			System.err.println("Failed to BatchSave() CompanyName Records");
			failedList.get(0).getException().printStackTrace();
		}
	}

	
	public static void main(String[] args) {
		NasdaqCompanyListDynamoDBReader reader = new NasdaqCompanyListDynamoDBReader();
		try {
			reader.processAllCompanies();
			reader.writeAllCompanies();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		System.exit(0);
	}
}
