package com.sanjoyghosh.company.dynamodb.model;

import java.time.LocalDate;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.sanjoyghosh.company.dynamodb.DynamoDBTypedConverters;

@DynamoDBTable(tableName="EarningsDate")
public class EarningsDate {

	@DynamoDBHashKey(attributeName="Date")
	@DynamoDBTypeConverted(converter=DynamoDBTypedConverters.LocalDateConverter.class)
	private LocalDate	date;
	
	@DynamoDBRangeKey(attributeName="Symbol")
	private String		symbol;
	
	@DynamoDBAttribute(attributeName="BeforeMarketOrAfterMarket")
	private String		beforeMarketOrAfterMarket;

	
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
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
