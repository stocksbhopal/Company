package com.sanjoyghosh.company.cloudwatch.logs;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.logs.AWSLogsAsync;
import com.amazonaws.services.logs.AWSLogsAsyncClient;
import com.amazonaws.services.logs.model.InputLogEvent;

public class CloudWatchLogger {

	private static final String GROUP_NAME = "FinanceHelper";
	private static final String STREAM_NAME = "IntentResult";
	private static final CloudWatchLogger instance = new CloudWatchLogger(GROUP_NAME, STREAM_NAME);
	
	
	private AWSLogsAsync		cloudWatchLoggerAsync;
	private String				groupName;
	private String				streamName;
	private boolean 			useLogEventListOne;
	private List<InputLogEvent>	logEventListOne;
	private List<InputLogEvent> logEventListTwo;
	
	
	private CloudWatchLogger(String groupName, String streamName) {
		this.groupName = groupName;
		this.streamName = streamName;
		this.useLogEventListOne = true;
		this.logEventListOne = new ArrayList<>();
		this.logEventListTwo = new ArrayList<>();

		cloudWatchLoggerAsync = new AWSLogsAsyncClient();
	}
	
	
	public static void init() {
		instance.ensureGroupAndStream();
	}


	public synchronized void ensureGroupAndStream() {
		// TODO: Ensure Group and Stream present.
	}

	
	public synchronized void flushLogEventList() {
		// flush the list.
		useLogEventListOne = !useLogEventListOne;
	}
	

	public synchronized void addLogEvent(CloudWatchLoggerIntentResult intentResult) {
		InputLogEvent logEvent = intentResult.toInputLogEvent();
		if (useLogEventListOne) {
			logEventListOne.add(logEvent);
		}
		else {
			logEventListTwo.add(logEvent);
		}
	}


	public static CloudWatchLogger getInstance() {
		return instance;
	}
}
