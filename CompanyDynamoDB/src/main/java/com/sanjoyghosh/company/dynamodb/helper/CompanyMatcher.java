package com.sanjoyghosh.company.dynamodb.helper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.sanjoyghosh.company.dynamodb.CompanyDynamoDB;
import com.sanjoyghosh.company.dynamodb.model.Company;
	
public class CompanyMatcher {

	private static List<Company> 		companyList;
	private static Map<String, Company>	companyBySymbolMap;
	
	
	public static void init() {
		AmazonDynamoDB amazonDynamoDB = CompanyDynamoDB.getAmazonDynamoDB();
		ScanRequest scanRequest = new ScanRequest().withTableName("Company");
		ScanResult scanResult = amazonDynamoDB.scan(scanRequest);
		
		DynamoDBMapper dynamoDBMapper = CompanyDynamoDB.getDynamoDBMapper();
		companyList = dynamoDBMapper.marshallIntoObjects(Company.class, scanResult.getItems());
		Collections.sort(companyList);
		
		companyBySymbolMap = new HashMap<>();
		for (Company company : companyList) {
			companyBySymbolMap.put(company.getSymbol(), company);
		}
	}
	
	
	public static Company getCompanyBySymbol(String symbol) {
		if (companyBySymbolMap == null) {
			init();
		}
		return companyBySymbolMap.get(symbol);
	}
	

	public static void main(String[] args) {
		CompanyMatcher.init();
	}
}
