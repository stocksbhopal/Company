package com.sanjoyghosh.company.logs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.earnings.intent.IntentResult;

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
			
			EntityManager em = null;
			try {
				em = JPAHelper.getEntityManager();
				em.getTransaction().begin();
				for (IntentResult intentResult : intentResultList) {
					em.persist(intentResult.toIntentResultLog());
				}
				em.getTransaction().commit();
			}
			catch (Throwable e) {
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
				logger.log(Level.SEVERE, "Exception persisting DB Logs", e);
			}
			finally {
				if (em != null) {
					em.close();
				}
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
