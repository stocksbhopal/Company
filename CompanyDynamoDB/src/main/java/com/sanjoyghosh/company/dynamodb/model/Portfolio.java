package com.sanjoyghosh.company.dynamodb.model;

import java.time.LocalDate;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.sanjoyghosh.company.dynamodb.DynamoDBTypedConverters;

@DynamoDBTable(tableName="Portfolio")
public class Portfolio {

	@DynamoDBHashKey(attributeName="AlexaUserId")
	private String		alexaUserId;
	
	@DynamoDBRangeKey(attributeName="Symbol")
	private String		symbol;
	
	@DynamoDBAttribute(attributeName="AddDate")
	@DynamoDBTypeConverted(converter=DynamoDBTypedConverters.LocalDateConverter.class)
	private LocalDate	addDate;
	
	@DynamoDBAttribute(attributeName="Quantity")
	private Double		quantity;

	
	public String getAlexaUserId() {
		return alexaUserId;
	}
	public void setAlexaUserId(String alexaUserId) {
		this.alexaUserId = alexaUserId;
	}

	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	
	public LocalDate getAddDate() {
		return addDate;
	}
	public void setAddDate(LocalDate addDate) {
		this.addDate = addDate;
	}

	
	public Double getQuantity() {
		return quantity;
	}
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
}
