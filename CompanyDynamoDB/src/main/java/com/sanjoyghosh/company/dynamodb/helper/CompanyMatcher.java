package com.sanjoyghosh.company.dynamodb.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.sanjoyghosh.company.dynamodb.CompanyDynamoDB;
import com.sanjoyghosh.company.dynamodb.model.Company;
	
public class CompanyMatcher {

	private static final Map<String, Company>		companyBySymbolMap = new HashMap<>();
	
	
	public static void init() {
		AmazonDynamoDB amazonDynamoDB = CompanyDynamoDB.getAmazonDynamoDB();
		ScanRequest scanRequest = new ScanRequest().withTableName("Company");
		ScanResult scanResult = amazonDynamoDB.scan(scanRequest);
		
		for (Map<String, AttributeValue> item : scanResult.getItems()) {
			for (Map.Entry<String, AttributeValue> entry : item.entrySet()) {
				System.out.println(entry.getKey() + "  " + entry.getValue().toString());
			}
			System.out.println();
		}
	}
	
	
	public static void main(String[] args) {
		CompanyMatcher.init();
	}
}
