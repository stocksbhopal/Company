package com.sanjoyghosh.company.dynamodb.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.sanjoyghosh.company.dynamodb.CompanyDynamoDB;
import com.sanjoyghosh.company.dynamodb.model.Company;
import com.sanjoyghosh.company.dynamodb.model.CompanyName;

public class CompanyNameMatcher {

//	private static final Logger			logger = Logger.getLogger("CompanyNameMatcher");
	private static Map<String, Company>	companyByNameMap;

	public static void init() {
		companyByNameMap = new HashMap<>();
		
		AmazonDynamoDB amazonDynamoDB = CompanyDynamoDB.getAmazonDynamoDB();
		ScanRequest scanRequest = new ScanRequest().withTableName("CompanyName");
		ScanResult scanResult = amazonDynamoDB.scan(scanRequest);
		
		DynamoDBMapper dynamoDBMapper = CompanyDynamoDB.getDynamoDBMapper();
		List<CompanyName> companyNameList = dynamoDBMapper.marshallIntoObjects(CompanyName.class, scanResult.getItems());
		
		companyByNameMap = new HashMap<>();
		for (CompanyName companyName : companyNameList) {
			Company company = CompanyMatcher.getCompanyBySymbol(companyName.getSymbol());
			if (company != null) {
				companyByNameMap.put(companyName.getNamePrefix(), company);
			}
		}
	}

	
	private static final Set<String> CompanyStopWords = new HashSet<>();
	static {
		CompanyStopWords.add("A".toLowerCase());
		CompanyStopWords.add("An".toLowerCase());
		CompanyStopWords.add("Co".toLowerCase());
		CompanyStopWords.add("Co.".toLowerCase());
		CompanyStopWords.add("Co.,".toLowerCase());
		CompanyStopWords.add("Companies".toLowerCase());
		CompanyStopWords.add("Company".toLowerCase());
		CompanyStopWords.add("Company,".toLowerCase());
		CompanyStopWords.add("Corp".toLowerCase());
		CompanyStopWords.add("Corp.".toLowerCase());
		CompanyStopWords.add("Corporation".toLowerCase());
		CompanyStopWords.add("El".toLowerCase());
		CompanyStopWords.add("Inc".toLowerCase());
		CompanyStopWords.add("Inc.".toLowerCase());
		CompanyStopWords.add("Incorporated".toLowerCase());
		CompanyStopWords.add("Limited".toLowerCase());
		CompanyStopWords.add("LLC".toLowerCase());
		CompanyStopWords.add("LTD".toLowerCase());
		CompanyStopWords.add("LTD.".toLowerCase());
		CompanyStopWords.add("L.P.".toLowerCase());
		CompanyStopWords.add("LP".toLowerCase());
		CompanyStopWords.add("LP.".toLowerCase());
		CompanyStopWords.add("N.P.".toLowerCase());
		CompanyStopWords.add("NV".toLowerCase());
		CompanyStopWords.add("PLC".toLowerCase());
		CompanyStopWords.add("PLC.".toLowerCase());
		CompanyStopWords.add("S.A.".toLowerCase());
		CompanyStopWords.add("SA".toLowerCase());
		CompanyStopWords.add("(The)".toLowerCase());
	}
	
	public static String stripStopWordsFromName(String name) {
		if (name == null || name.trim().length() == 0) {
			return null;
		}
		name = name.trim().toLowerCase();
		
		// THE FOLLOWING REPLACEMENTS HAVE TO BE DONE
		// IN THE SAME ORDER.
		// archer-daniels-midland company
		name = name.replaceAll("-", " ");
		// apostrophe
		name = name.replaceAll("&#39;", "");
		// amazon.com
		name = name.replaceAll("\\.com ", " ");
		// home depot, inc. (the)
		name = name.replaceAll(",", "");
		// remove the .s for name abbreviations
		name = name.replaceAll("\\.", "");
		// alexa doesn't know &. expand with blanks
		name = name.replaceAll(" & ", " and ");
		// alexa doesn't know &. expand with blanks
		name = name.replaceAll("&", "and");
		
		String[] pieces = name.split(" ");
		int length = pieces.length;
		String strippedName = "";
		for (int i = 0; i < length; i++) {
			String word = pieces[i];
			if (!CompanyStopWords.contains(word)) {
				strippedName += word + " ";
			}
		}

		name = strippedName.length() > 0 ? strippedName.trim() : name;
		// Make sure the name doesn't end with "and" after stripping the stop words.
		name = name.endsWith("and") ? name.substring(0,  name.length() - "and".length()).trim() : name;
		
		return name;
	}
	
	
	private static final Map<String, List<Company>> companyListByNameMap = new HashMap<>();
	// Assume that the name is already trimmed and lower-cased.
	// The given name may be different from the actual name of the company
	// when we add popular names for companies.  Such as Google for Alphabet.
	public static void processStrippedName(String name, Company company) {
		String[] pieces = name.split(" ");
		String partialName = "";
		for (int i = 0; i < pieces.length; i++) {
			partialName = partialName + " " + pieces[i];
			partialName = partialName.trim();
			
			List<Company> companyList = companyListByNameMap.get(partialName);
			if (companyList == null) {
				companyList = new ArrayList<>();
				companyListByNameMap.put(partialName, companyList);
			}
			companyList.add(company);
		}
	}
	
	
	public static void assignNamePrefixToCompany(List<CompanyName> companyNameList) {
		companyByNameMap = new HashMap<>();
		for (Map.Entry<String, List<Company>> entry : companyListByNameMap.entrySet()) {
			String namePrefix = entry.getKey();
			
			Company company = null;
			List<Company> companyList = entry.getValue();
			if (companyList.size() == 1) {
				company = companyList.get(0);
			}
			else {
				for (Company companyTemp : companyList) {
					if (companyTemp.getNameStripped().equals(namePrefix)) {
						company = companyTemp;
						break;
					}
				}
			}
			
			if (company != null) {
				companyByNameMap.put(namePrefix, company);
				
				CompanyName nameMatch = new CompanyName();
				nameMatch.setNamePrefix(namePrefix);
				nameMatch.setSymbol(company.getSymbol());
				companyNameList.add(nameMatch);
				
				System.out.println("ADD: " + namePrefix + ": " + company.getSymbol());
			}
		}
		return;
	}
	
	
	public static String getSymbolSpelt(String symbolSpelt) {
		String symbol = "";
		String[] pieces = symbolSpelt.split(" ");
		for (String piece : pieces) {
			symbol += piece.charAt(0);
		}
		symbol = symbol.trim().toUpperCase();
		return symbol;
	}
	
	
	public static Company getCompanyByNameOrSymbol(String companyOrSymbol) {
		if (companyByNameMap == null) {
			init();
		}
		
    		companyOrSymbol = stripStopWordsFromName(companyOrSymbol);
    		String symbolSpelt = getSymbolSpelt(companyOrSymbol);

    		Company company = companyByNameMap.get(companyOrSymbol.toLowerCase());
    		if (company != null) {
    			return company;
    		}

    		company = CompanyMatcher.getCompanyBySymbol(symbolSpelt);
    		if (company != null) {
    			return company;
    		}

    		company = CompanyMatcher.getCompanyBySymbol(companyOrSymbol);
    		if (company != null) {
    			return company;
    		}
    		
    		company = companyByNameMap.get(symbolSpelt.toLowerCase());
    		if (company != null) {
    			return company;
    		}
    		
    		return null;
	}
}
