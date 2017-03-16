package com.sanjoyghosh.company.logs;

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
import com.sanjoyghosh.company.db.JPAHelper;

public class CloudWatchLogger {

	private static final String GROUP_NAME = "FinanceHelper";
	private static final String STREAM_NAME_PREFIX = "IntentResult-";
	private static final CloudWatchLogger instance = new CloudWatchLogger(GROUP_NAME);
	
    private static final Logger logger = Logger.getLogger(CloudWatchLogger.class.getName());
	
	
	private AWSLogs								cloudWatchLogger;
	private String								streamName;
	private String								nextSequenceToken;
	private boolean 							useLogEventListOne;
	private List<CloudWatchLoggerIntentResult>	intentResultListOne;
	private List<CloudWatchLoggerIntentResult>	intentResultListTwo;
	
	
	@SuppressWarnings("deprecation")
	private CloudWatchLogger(String groupName) {
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
					instance.flushLogEventList();
				}
				catch (Throwable e) {
					logger.log(Level.SEVERE, "Exception in flushLogEventList()", e);
				}
			}
		}, 5, 5, TimeUnit.SECONDS);
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
		List<CloudWatchLoggerIntentResult> intentResultList = useLogEventListOne ? intentResultListTwo : intentResultListOne;
		if (intentResultList.size() > 0 && ensureGroupAndStream()) {
			
			List<InputLogEvent> logEventList = new ArrayList<>();
			JPAHelper.getEntityManagerLogs().getTransaction().begin();
			try {
				for (CloudWatchLoggerIntentResult intentResult : intentResultList) {
					IntentResultLog intentResultLog = intentResult.toIntentResultLog();
					JPAHelper.getEntityManagerLogs().persist(intentResultLog);
					
					InputLogEvent logEvent = intentResult.toInputLogEvent();
					logEventList.add(logEvent);
				}
				JPAHelper.getEntityManagerLogs().getTransaction().commit();
			}
			catch (Throwable e) {
				JPAHelper.getEntityManagerLogs().getTransaction().rollback();
				logger.log(Level.SEVERE, "Exception persisting CloudWatch Logs", e);
			}
			
			PutLogEventsRequest logRequest = new PutLogEventsRequest();
			logRequest.setLogEvents(logEventList);
			logRequest.setLogGroupName(GROUP_NAME);
			logRequest.setLogStreamName(streamName);
			logRequest.setSequenceToken(nextSequenceToken);
	
			PutLogEventsResult logEventsResult = cloudWatchLogger.putLogEvents(logRequest);
			nextSequenceToken = logEventsResult.getNextSequenceToken();
			intentResultList.clear();
		}
	}
	

	public synchronized void addLogEvent(CloudWatchLoggerIntentResult intentResult) {
		if (useLogEventListOne) {
			intentResultListOne.add(intentResult);
		}
		else {
			intentResultListTwo.add(intentResult);
		}
	}


	public static CloudWatchLogger getInstance() {
		return instance;
	}
}
