package com.sanjoyghosh.company.logs;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClient;
import com.amazonaws.services.logs.model.CreateLogGroupRequest;
import com.amazonaws.services.logs.model.CreateLogStreamRequest;
import com.amazonaws.services.logs.model.InputLogEvent;
import com.amazonaws.services.logs.model.PutLogEventsRequest;
import com.amazonaws.services.logs.model.PutLogEventsResult;
import com.amazonaws.services.logs.model.ResourceAlreadyExistsException;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.IntentResultLog;

public class IntentResultLogger {

	private static IntentResultLogger instance = null;
	
    private static final Logger logger = Logger.getLogger(IntentResultLogger.class.getName());
	
	
	private boolean 							useLogEventListOne;
	private List<CloudWatchLoggerIntentResult>	intentResultListOne;
	private List<CloudWatchLoggerIntentResult>	intentResultListTwo;
	
	
	@SuppressWarnings("deprecation")
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
		List<CloudWatchLoggerIntentResult> intentResultList = useLogEventListOne ? intentResultListTwo : intentResultListOne;
		if (intentResultList.size() > 0) {
			
			EntityManager em = null;
			List<InputLogEvent> logEventList = new ArrayList<>();
			try {
				em = JPAHelper.getEntityManager();
				em.getTransaction().begin();
				for (CloudWatchLoggerIntentResult intentResult : intentResultList) {
					IntentResultLog intentResultLog = intentResult.toIntentResultLog();
					em.persist(intentResultLog);
					
					InputLogEvent logEvent = intentResult.toInputLogEvent();
					logEventList.add(logEvent);
				}
				em.getTransaction().commit();
			}
			catch (Throwable e) {
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
				logger.log(Level.SEVERE, "Exception persisting CloudWatch Logs", e);
			}
			finally {
				if (em != null) {
					em.close();
				}
			}
			intentResultList.clear();
		}
	}
	

	public synchronized void addLogEvent(CloudWatchLoggerIntentResult intentResult, String juliLoggerMessage) {
//		addLogEvent(intentResult, juliLoggerMessage, null);
	}


	public synchronized void addLogEvent(IntentResult intentResult, String juliLoggerMessage, Throwable e) {
		if (juliLoggerMessage != null && e != null) {
			logger.log(Level.SEVERE, juliLoggerMessage, e);
		}
		else if (juliLoggerMessage != null) {
			logger.info(juliLoggerMessage);
		}
		
		if (useLogEventListOne) {
//			intentResultListOne.add(intentResult);
		}
		else {
//			intentResultListTwo.add(intentResult);
		}
	}

	
	public synchronized static IntentResultLogger getInstance() {
		if (instance == null) {
			instance = new IntentResultLogger();
		}
		return instance;
	}
}
