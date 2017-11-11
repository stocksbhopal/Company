package com.sanjoyghosh.company.dynamodb.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="CompanyNameMatch")
public class CompanyNameMatch {

	@DynamoDBHashKey(attributeName="NamePrefix")
	private String	namePrefix;
	
	@DynamoDBRangeKey(attributeName="Symbol")
	private String	symbol;
	
	
	public String getNamePrefix() {
		return namePrefix;
	}
	public void setNamePrefix(String namePrefix) {
		this.namePrefix = namePrefix;
	}
	
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
}
