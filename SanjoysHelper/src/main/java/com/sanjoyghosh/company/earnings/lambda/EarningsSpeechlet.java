package com.sanjoyghosh.company.earnings.lambda;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.sanjoyghosh.company.earnings.intent.IntentGetStockEarnings;
import com.sanjoyghosh.company.earnings.intent.IntentGetStockPrice;
import com.sanjoyghosh.company.earnings.intent.IntentMyStocksMovers;
import com.sanjoyghosh.company.earnings.intent.IntentMyStocksStatus;
import com.sanjoyghosh.company.earnings.intent.IntentResult;
import com.sanjoyghosh.company.earnings.intent.IntentStockOnList;
import com.sanjoyghosh.company.earnings.intent.IntentStopCancel;
import com.sanjoyghosh.company.earnings.intent.IntentUpdatePrices;
import com.sanjoyghosh.company.earnings.intent.InterfaceIntent;
import com.sanjoyghosh.company.earnings.intent.LaunchSanjoysHelper;
import com.sanjoyghosh.company.logs.IntentResultLogger;

public class EarningsSpeechlet implements Speechlet  {

    private static final Logger logger = Logger.getLogger(EarningsSpeechlet.class.getName());

    private static final Map<String, InterfaceIntent> intentInterfaceByIntentNameMap = new HashMap<>();
    static {    	
    	intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_EARNINGS_ON_LIST, new IntentGetStockEarnings());

    	intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_CREATE_STOCK_ON_LIST, new IntentStockOnList());
    	intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_READ_STOCK_ON_LIST, new IntentStockOnList());
    	intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_UPDATE_STOCK_ON_LIST, new IntentStockOnList());
    	intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_DELETE_STOCK_ON_LIST, new IntentStockOnList());
    	intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_LIST_STOCKS_ON_LIST, new IntentStockOnList());
    	intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_CLEAR_STOCKS_ON_LIST, new IntentStockOnList());

    	intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_GET_STOCK_PRICE, new IntentGetStockPrice());
		
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_MY_STOCKS_STATUS, new IntentMyStocksStatus());
		
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_MY_STOCKS_GAINERS, new IntentMyStocksMovers());
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_MY_STOCKS_LOSERS, new IntentMyStocksMovers());
		
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_UPDATE_PRICES, new IntentUpdatePrices());
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_AMAZON_STOP_INTENT, new IntentStopCancel());
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_AMAZON_CANCEL_INTENT, new IntentStopCancel());
    }
        
    
    public static void init() {
    	IntentResultLogger.init();
    }
    
	
    @Override
	public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
    }


	@Override
	public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
		SpeechletResponse response = LaunchSanjoysHelper.onLaunch(session);
		return response;
	}

	
	// This method also throws lots of RuntimeExceptions such as SQLException.
	// If there is a RuntimeException this will be tried again.
	private SpeechletResponse tryOnIntent(IntentRequest request, Session session, IntentResult intentResult) throws SpeechletException {
		String intentName = request.getIntent().getName();

		if (intentName.equals(InterfaceIntent.INTENT_AMAZON_HELP_INTENT)) {
			return LaunchSanjoysHelper.onLaunch(session);
		}
				
		if (intentName.equals(InterfaceIntent.INTENT_AMAZON_YES_INTENT) || intentName.equals(InterfaceIntent.INTENT_AMAZON_NO_INTENT)) {
			String lastIntentName = (String) session.getAttribute(InterfaceIntent.ATTR_LAST_INTENT);
			if (lastIntentName != null) {
				InterfaceIntent interfaceIntent = intentInterfaceByIntentNameMap.get(lastIntentName);
				if (interfaceIntent != null) {
					return interfaceIntent.onIntent(request, session, intentResult);
				}				
			}
		}

		// This check has to happen after the check for YesIntent and NoIntent since those will always
		// have session.isNew() == false.  They are used for user confirmation.
		if (!session.isNew() && 
			!intentName.equals(InterfaceIntent.INTENT_GET_STOCK_PRICE) &&
			!intentName.equals(InterfaceIntent.INTENT_AMAZON_CANCEL_INTENT) &&
			!intentName.equals(InterfaceIntent.INTENT_AMAZON_STOP_INTENT)) {
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText("Sorry, when you open Finance Helper you can only ask the price of a stock, or say Stop or Cancel or Exit.");
			return SpeechletResponse.newTellResponse(outputSpeech);					
		}

		InterfaceIntent interfaceIntent = intentInterfaceByIntentNameMap.get(intentName);
		if (interfaceIntent != null) {
			return interfaceIntent.onIntent(request, session, intentResult);
		}

		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("Finance Helper has no idea what to do with this intent: " + intentName);
		return SpeechletResponse.newTellResponse(outputSpeech);		
	}
	
	
	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		IntentResult intentResult = new IntentResult(request, session);
		try {
			for (int retries = 0; retries < 5; retries++) {
				try {
					return tryOnIntent(request, session, intentResult);
				}
				catch (PersistenceException e) {
					logger.log(Level.SEVERE, "PersistenceException in EarningsSpeechlet.onIntent(), retries:" + retries, e);
					Thread.sleep(10);
				}
			}
		}
		catch (SpeechletException e) {
			logger.log(Level.SEVERE, "SpeechletException in EarningsSpeechlet.onIntent()", e);
			throw e;
		}
		catch (Throwable e) {
			logger.log(Level.SEVERE, "Throwable in EarningsSpeechlet.onIntent()", e);
			throw new SpeechletException(e);
		}
		finally {
			intentResult.setExecTimeMilliSecs((int) (System.currentTimeMillis() - intentResult.getEventTime().getTime()));
			IntentResultLogger.getInstance().addLogEvent(intentResult);
		}
		
		throw new SpeechletException("Too many retries");
	}

	
	@Override
	public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
	}
}
