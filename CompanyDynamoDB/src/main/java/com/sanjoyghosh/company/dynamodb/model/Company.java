package com.sanjoyghosh.company.dynamodb.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBVersionAttribute;

@DynamoDBTable(tableName="Company")
public class Company {

	@DynamoDBHashKey(attributeName="Symbol")
	private String	symbol;

	@DynamoDBRangeKey(attributeName="Name")
	private String	name;

	@DynamoDBAttribute(attributeName="IPOYear")
	private Integer	ipoYear;
	
	@DynamoDBAttribute(attributeName="Exchange")
	private String	exchange;
	
	@DynamoDBAttribute(attributeName="Sector")
	private String	sector;
	
	@DynamoDBAttribute(attributeName="Industry")
	private String	industry;
	
	@DynamoDBVersionAttribute(attributeName="Version")
	private Integer	version;
	
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getIpoYear() {
		return ipoYear;
	}
	public void setIpoYear(Integer ipoYear) {
		this.ipoYear = ipoYear;
	}
	
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
	}
	
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
}
