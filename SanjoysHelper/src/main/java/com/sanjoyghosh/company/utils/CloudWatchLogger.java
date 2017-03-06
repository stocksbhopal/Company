package com.sanjoyghosh.company.utils;

import com.amazonaws.services.logs.AWSLogsAsync;
import com.amazonaws.services.logs.AWSLogsAsyncClient;

public class CloudWatchLogger {

	private static CloudWatchLogger instance;
	
	private AWSLogsAsync cloudWatchLoggerAsync;
	
	
	private CloudWatchLogger() {
		cloudWatchLoggerAsync = new AWSLogsAsyncClient();
	}
	
	
	public static void init() {
		if (instance == null) {
			instance = new CloudWatchLogger();
		}
	}


	public static CloudWatchLogger getInstance() {
		return instance;
	}


	public AWSLogsAsync getCloudWatchLoggerAsync() {
		return cloudWatchLoggerAsync;
	}
}
