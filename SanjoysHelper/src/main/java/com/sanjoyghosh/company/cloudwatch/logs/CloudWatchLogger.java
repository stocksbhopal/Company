package com.sanjoyghosh.company.cloudwatch.logs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClient;
import com.amazonaws.services.logs.model.CreateLogGroupRequest;
import com.amazonaws.services.logs.model.CreateLogStreamRequest;
import com.amazonaws.services.logs.model.InputLogEvent;
import com.amazonaws.services.logs.model.PutLogEventsRequest;
import com.amazonaws.services.logs.model.PutLogEventsResult;
import com.amazonaws.services.logs.model.ResourceAlreadyExistsException;

public class CloudWatchLogger {

	private static final String GROUP_NAME = "FinanceHelper";
	private static final String STREAM_NAME_PREFIX = "IntentResult-";
	private static final CloudWatchLogger instance = new CloudWatchLogger(GROUP_NAME);
	
    private static final Logger logger = Logger.getLogger(CloudWatchLogger.class.getName());
	
	
	private AWSLogs				cloudWatchLogger;
	private String				streamName;
	private String				nextSequenceToken;
	private boolean 			useLogEventListOne;
	private List<InputLogEvent>	logEventListOne;
	private List<InputLogEvent> logEventListTwo;
	
	
	@SuppressWarnings("deprecation")
	private CloudWatchLogger(String groupName) {
		this.useLogEventListOne = true;
		this.logEventListOne = new ArrayList<>();
		this.logEventListTwo = new ArrayList<>();

		cloudWatchLogger = new AWSLogsClient();
	}
	
	
	public static void init() {
		ScheduledThreadPoolExecutor poolExecutor = new ScheduledThreadPoolExecutor(1);
		poolExecutor.scheduleWithFixedDelay(new Runnable() {			
			@Override
			public void run() {
				instance.flushLogEventList();
			}
		}, 2, 2, TimeUnit.SECONDS);
	}


	public synchronized boolean ensureGroupAndStream() {
		try {
			CreateLogGroupRequest groupRequest = new CreateLogGroupRequest(GROUP_NAME);
			cloudWatchLogger.createLogGroup(groupRequest);
		}
		catch (ResourceAlreadyExistsException e) {}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to create log group: " + GROUP_NAME, e);
			return false;
		}
		
		if (streamName == null) {
			streamName = STREAM_NAME_PREFIX + System.currentTimeMillis();
		}
		try {
			CreateLogStreamRequest streamRequest = new CreateLogStreamRequest(GROUP_NAME, streamName);
			cloudWatchLogger.createLogStream(streamRequest);
			nextSequenceToken = null;
		}
		catch (ResourceAlreadyExistsException e) {}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to create log stream: " + streamName, e);
			return false;
		}
		
		return true;
	}
	
		
	// This method is only invoked from the Timer thread.
	public void flushLogEventList() {
		synchronized (this) {
			useLogEventListOne = !useLogEventListOne;			
		}
		
		// Log the list that is NOT pointed to by useLogEventListOne.
		List<InputLogEvent> list = useLogEventListOne ? logEventListTwo : logEventListOne;
		if (list.size() > 0 && ensureGroupAndStream()) {
			PutLogEventsRequest logRequest = new PutLogEventsRequest();
			logRequest.setLogEvents(list);
			logRequest.setLogGroupName(GROUP_NAME);
			logRequest.setLogStreamName(streamName);
			logRequest.setSequenceToken(nextSequenceToken);
	
			PutLogEventsResult logEventsResult = cloudWatchLogger.putLogEvents(logRequest);
			nextSequenceToken = logEventsResult.getNextSequenceToken();
			list.clear();
		}
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
