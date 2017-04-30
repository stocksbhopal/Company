package com.sanjoyghosh.company.logs;

import java.net.Inet4Address;
import java.net.UnknownHostException;
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

	private static final String GROUP_NAME_PREFIX = "FinanceHelper-";
	private static final String STREAM_NAME_PREFIX = "IntentResult-";
	private static CloudWatchLogger instance = null;
	
    private static final Logger logger = Logger.getLogger(CloudWatchLogger.class.getName());
	
	
	private AWSLogs								cloudWatchLogger;
	private String								groupName;
	private String								streamName;
	private String								nextSequenceToken;
	private boolean 							useLogEventListOne;
	private List<CloudWatchLoggerIntentResult>	intentResultListOne;
	private List<CloudWatchLoggerIntentResult>	intentResultListTwo;
	
	
	@SuppressWarnings("deprecation")
	private CloudWatchLogger(String groupNamePrefix) {
		try {
			this.groupName = groupNamePrefix + Inet4Address.getLocalHost().getHostName();
		} 
		catch (UnknownHostException e) {
			logger.log(Level.SEVERE, "Exception in getting local host name", e);
		}
		this.useLogEventListOne = true;
		this.intentResultListOne = new ArrayList<>();
		this.intentResultListTwo = new ArrayList<>();

		cloudWatchLogger = new AWSLogsClient();
	}
	
	
	public static void init() {
		ScheduledThreadPoolExecutor poolExecutor = new ScheduledThreadPoolExecutor(5);
		poolExecutor.scheduleWithFixedDelay(new Runnable() {			
			@Override
			public void run() {
				try {
					getInstance().flushLogEventList();
				}
				catch (Throwable e) {
					logger.log(Level.SEVERE, "Exception in flushLogEventList()", e);
				}
			}
		}, 5, 5, TimeUnit.SECONDS);
	}


	public synchronized boolean ensureGroupAndStream() {
		try {
			CreateLogGroupRequest groupRequest = new CreateLogGroupRequest(groupName);
			cloudWatchLogger.createLogGroup(groupRequest);
		}
		catch (ResourceAlreadyExistsException e) {}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to create log group: " + groupName, e);
			return false;
		}
		
		if (streamName == null) {
			streamName = STREAM_NAME_PREFIX + System.currentTimeMillis();
		}
		try {
			CreateLogStreamRequest streamRequest = new CreateLogStreamRequest(groupName, streamName);
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
		List<CloudWatchLoggerIntentResult> intentResultList = useLogEventListOne ? intentResultListTwo : intentResultListOne;
		if (intentResultList.size() > 0 && ensureGroupAndStream()) {
			
			List<InputLogEvent> logEventList = new ArrayList<>();
			for (CloudWatchLoggerIntentResult intentResult : intentResultList) {
				logEventList.add(intentResult.toInputLogEvent());
			}
			PutLogEventsRequest logRequest = new PutLogEventsRequest();
			logRequest.setLogEvents(logEventList);
			logRequest.setLogGroupName(groupName);
			logRequest.setLogStreamName(streamName);
			logRequest.setSequenceToken(nextSequenceToken);
	
			PutLogEventsResult logEventsResult = cloudWatchLogger.putLogEvents(logRequest);
			nextSequenceToken = logEventsResult.getNextSequenceToken();
			intentResultList.clear();
		}
	}
	

	public synchronized void addLogEvent(CloudWatchLoggerIntentResult intentResult, String juliLoggerMessage) {
		addLogEvent(intentResult, juliLoggerMessage, null);
	}


	public synchronized void addLogEvent(CloudWatchLoggerIntentResult intentResult, String juliLoggerMessage, Throwable e) {
		if (juliLoggerMessage != null && e != null) {
			logger.log(Level.SEVERE, juliLoggerMessage, e);
		}
		else if (juliLoggerMessage != null) {
			logger.info(juliLoggerMessage);
		}
		
		if (useLogEventListOne) {
			intentResultListOne.add(intentResult);
		}
		else {
			intentResultListTwo.add(intentResult);
		}
	}

	
	public synchronized static CloudWatchLogger getInstance() {
		if (instance == null) {
			instance = new CloudWatchLogger(GROUP_NAME_PREFIX);
		}
		return instance;
	}
}
