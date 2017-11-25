package com.sanjoyghosh.company.alexaskill.intent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sanjoyghosh.company.dynamodb.CompanyDynamoDB;
import com.sanjoyghosh.company.dynamodb.model.IntentResultLog;

public class IntentResultLogger {

	private static IntentResultLogger instance = null;
	
    private static final Logger logger = Logger.getLogger(IntentResultLogger.class.getName());
	
	
	private boolean 			useLogEventListOne;
	private List<IntentResult>	intentResultListOne;
	private List<IntentResult>	intentResultListTwo;
	
	
	private IntentResultLogger() {
		this.useLogEventListOne = true;
		this.intentResultListOne = new ArrayList<>();
		this.intentResultListTwo = new ArrayList<>();
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
	
		
	// This method is only invoked from the Timer thread.
	public void flushLogEventList() {
		synchronized (this) {
			useLogEventListOne = !useLogEventListOne;			
		}
		
		// Log the list that is NOT pointed to by useLogEventListOne.
		List<IntentResult> intentResultList = useLogEventListOne ? intentResultListTwo : intentResultListOne;
		if (intentResultList.size() > 0) {
			List<IntentResultLog> intentResultLogList = new ArrayList<>();
			for (IntentResult intentResult : intentResultList) {
				intentResultLogList.add(intentResult.toIntentResultLog());
			}
			try {
				CompanyDynamoDB.batchSaveDynamoDB((Iterable) intentResultLogList, "IntentResultLog");
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			intentResultList.clear();
		}
	}
	

	public synchronized void addLogEvent(IntentResult intentResultLog) {
		if (useLogEventListOne) {
			intentResultListOne.add(intentResultLog);
		}
		else {
			intentResultListTwo.add(intentResultLog);
		}
	}

	
	public synchronized static IntentResultLogger getInstance() {
		if (instance == null) {
			instance = new IntentResultLogger();
		}
		return instance;
	}
}
