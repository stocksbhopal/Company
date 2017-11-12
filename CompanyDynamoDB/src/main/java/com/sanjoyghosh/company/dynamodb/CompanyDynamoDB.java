package com.sanjoyghosh.company.dynamodb;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class CompanyDynamoDB {

	private static AmazonDynamoDB	amazonDynamoDB;
	private static DynamoDBMapper	dynamoDBMapper;

	private CompanyDynamoDB() {}
	
	public synchronized static DynamoDBMapper getDynamoDBMapper() {
		if (dynamoDBMapper == null) {
			amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
				.withCredentials(new ProfileCredentialsProvider())
				.withRegion(Regions.US_EAST_1).build();
			dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
		}
		return dynamoDBMapper;
	}

	public synchronized static AmazonDynamoDB getAmazonDynamoDB() {
		if (amazonDynamoDB == null) {
			amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
				.withCredentials(new ProfileCredentialsProvider())
				.withRegion(Regions.US_EAST_1).build();
			dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
		}
		return amazonDynamoDB;
	}
}
