package com.sanjoyghosh.company.dynamodb.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="EarningsDate")
public class EarningsDate {

	@DynamoDBHashKey(attributeName="Date")
	private String	date;
	
	@DynamoDBRangeKey(attributeName="Symbol")
	private String	symbol;
	
	@DynamoDBAttribute(attributeName="BeforeMarketOrAfterMarket")
	private String	beforeMarketOrAfterMarket;

	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	
	public String getBeforeMarketOrAfterMarket() {
		return beforeMarketOrAfterMarket;
	}
	public void setBeforeMarketOrAfterMarket(String beforeMarketOrAfterMarket) {
		this.beforeMarketOrAfterMarket = beforeMarketOrAfterMarket;
	}
}
