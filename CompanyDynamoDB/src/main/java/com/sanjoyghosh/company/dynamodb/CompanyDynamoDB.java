package com.sanjoyghosh.company.dynamodb;

import java.io.File;
import java.util.List;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfilesConfigFile;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class CompanyDynamoDB {

	private static AmazonDynamoDB	amazonDynamoDB;
	private static DynamoDBMapper	dynamoDBMapper;

	private CompanyDynamoDB() {}

	
	public synchronized static AmazonDynamoDB getAmazonDynamoDB() {
		if (amazonDynamoDB == null) {
			File credentialsFile = new File("/home/ubuntu/credentials");
			if (credentialsFile.exists() && credentialsFile.isFile()) {
				amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
					.withCredentials(new ProfileCredentialsProvider(new ProfilesConfigFile(credentialsFile), "default"))
					.withRegion(Regions.US_EAST_1).build();				
			}
			else {
				amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
					.withCredentials(new ProfileCredentialsProvider())
					.withRegion(Regions.US_EAST_1).build();
			}
			dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
		}
		return amazonDynamoDB;
	}
	

	public synchronized static DynamoDBMapper getDynamoDBMapper() {
		if (dynamoDBMapper == null) {
			amazonDynamoDB = getAmazonDynamoDB();
			dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
		}
		return dynamoDBMapper;
	}

	
	public static <T> void batchSaveDynamoDB(Iterable<T> earningsDateList, String objectName) throws Exception {
		if (earningsDateList == null) {
			return;
		}
		
		long startTime = System.currentTimeMillis();
		System.out.println("Before DynamoDB Batch Save: " + objectName);
		{
			List<DynamoDBMapper.FailedBatch> failedList = dynamoDBMapper.batchSave(earningsDateList);
			if (failedList.size() > 0) {
				System.err.println("Failed DynamoDB Batch Save: " + objectName + ", Size: " + failedList.size());
				throw failedList.get(0).getException();
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("After DynamoDB Batch Save: " + objectName + ", Time: " + (endTime - startTime) + " msecs");
	}
	
	
	public static <T> void batchDeleteDynamoDB(Iterable<T> objectIterable, String objectName) throws Exception {
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
