package com.sanjoyghosh.company.cloudwatch.logs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClient;
import com.amazonaws.services.logs.model.InputLogEvent;
import com.amazonaws.services.logs.model.PutLogEventsRequest;
import com.amazonaws.services.logs.model.PutLogEventsResult;
import com.sanjoyghosh.company.earnings.intent.IntentGetStockPrice;

public class CloudWatchLogger {

	private static final String GROUP_NAME = "FinanceHelper";
	private static final String STREAM_NAME = "IntentResult";
	private static final CloudWatchLogger instance = new CloudWatchLogger(GROUP_NAME, STREAM_NAME);
	
    private static final Logger logger = Logger.getLogger(CloudWatchLogger.class.getPackage().getName());
	
	
	private AWSLogs				cloudWatchLogger;
	private String				nextSequenceToken;
	private String				groupName;
	private String				streamName;
	private boolean 			useLogEventListOne;
	private List<InputLogEvent>	logEventListOne;
	private List<InputLogEvent> logEventListTwo;
	
	
	@SuppressWarnings("deprecation")
	private CloudWatchLogger(String groupName, String streamName) {
		this.groupName = groupName;
		this.streamName = streamName;
		this.useLogEventListOne = true;
		this.logEventListOne = new ArrayList<>();
		this.logEventListTwo = new ArrayList<>();

		cloudWatchLogger = new AWSLogsClient();
	}
	
	
	public static void init() {
		instance.ensureGroupAndStream();
		
		ScheduledThreadPoolExecutor poolExecutor = new ScheduledThreadPoolExecutor(1);
		poolExecutor.scheduleWithFixedDelay(new Runnable() {			
			@Override
			public void run() {
				instance.flushLogEventList();
			}
		}, 2, 2, TimeUnit.SECONDS);
	}


	public synchronized void ensureGroupAndStream() {
		// TODO: Ensure Group and Stream present.
	}

	
	// This method is only invoked from the Timer thread.
	public void flushLogEventList() {
		synchronized (this) {
			useLogEventListOne = !useLogEventListOne;			
		}
		
		// Log the list that is NOT pointed to by useLogEventListOne.
		List<InputLogEvent> list = useLogEventListOne ? logEventListTwo : logEventListOne;
		if (list.size() > 0) {
			PutLogEventsRequest logRequest = new PutLogEventsRequest();
			logRequest.setLogEvents(list);
			logRequest.setLogGroupName(GROUP_NAME);
			logRequest.setLogStreamName(STREAM_NAME);
			logRequest.setSequenceToken(nextSequenceToken);
	
			PutLogEventsResult logEventsResult = cloudWatchLogger.putLogEvents(logRequest);
			list.clear();
			nextSequenceToken = logEventsResult.getNextSequenceToken();
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
