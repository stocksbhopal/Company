package com.sanjoyghosh.company.dynamodb;

import java.util.List;

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
	
	
	public static void batchSaveDynamoDB(Iterable<Object> objectIterable, String objectName) throws Exception {
		if (objectIterable == null) {
			return;
		}
		
		long startTime = System.currentTimeMillis();
		System.out.println("Before DynamoDB Batch Save: " + objectName);
		{
			List<DynamoDBMapper.FailedBatch> failedList = dynamoDBMapper.batchSave(objectIterable);
			if (failedList.size() > 0) {
				System.err.println("Failed DynamoDB Batch Save: " + objectName + ", Size: " + failedList.size());
				throw failedList.get(0).getException();
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("After DynamoDB Batch Save: " + objectName + ", Time: " + (endTime - startTime) + " msecs");
	}
	
	
	public static void batchDeleteDynamoDB(Iterable<Object> objectIterable, String objectName) throws Exception {
		long startTime = System.currentTimeMillis();
		System.out.println("Before DynamoDB Batch Delete: " + objectName);
		{
			List<DynamoDBMapper.FailedBatch> failedList = dynamoDBMapper.batchDelete(objectIterable);
			if (failedList.size() > 0) {
				System.err.println("Failed DynamoDB Batch Delete: " + objectName + ", Size: " + failedList.size());
				throw failedList.get(0).getException();
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("After DynamoDB Batch Delete: " + objectName + ", Time: " + (endTime - startTime) + " msecs");
	}

	
	public static void saveObject(Object object) {
		dynamoDBMapper.save(object);
	}
}
