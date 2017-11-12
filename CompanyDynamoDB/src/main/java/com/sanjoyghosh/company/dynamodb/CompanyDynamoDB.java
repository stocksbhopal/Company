package com.sanjoyghosh.company.dynamodb;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class CompanyDynamoDB {

	private static DynamoDBMapper	dynamoDBMapper;

	private CompanyDynamoDB() {}
	
	public static DynamoDBMapper getDynamoDBMapper() {
		if (dynamoDBMapper == null) {
			AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.standard()
				.withCredentials(new ProfileCredentialsProvider())
				.withRegion(Regions.US_EAST_1).build();
			dynamoDBMapper = new DynamoDBMapper(dynamoDB);
		}
		return dynamoDBMapper;
	}
}
