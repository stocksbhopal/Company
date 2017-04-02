package com.sanjoyghosh.company.earnings.lambda;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.sanjoyghosh.company.earnings.intent.IntentGetMyStocks;
import com.sanjoyghosh.company.earnings.intent.IntentGetMyStocksWithEarnings;
import com.sanjoyghosh.company.earnings.intent.IntentGetStockPrice;
import com.sanjoyghosh.company.earnings.intent.IntentMyStocksMovers;
import com.sanjoyghosh.company.earnings.intent.IntentMyStocksStatus;
import com.sanjoyghosh.company.earnings.intent.IntentStockOnList;
import com.sanjoyghosh.company.earnings.intent.IntentStopCancel;
import com.sanjoyghosh.company.earnings.intent.IntentUpdatePrices;
import com.sanjoyghosh.company.earnings.intent.InterfaceIntent;
import com.sanjoyghosh.company.earnings.intent.LaunchSanjoysHelper;
import com.sanjoyghosh.company.logs.CloudWatchLogger;

public class EarningsSpeechlet implements Speechlet  {

    private static final Logger logger = Logger.getLogger(EarningsSpeechlet.class.getName());

    private static final Map<String, InterfaceIntent> intentInterfaceByIntentNameMap = new HashMap<>();
    static {    	
    	intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_GET_MY_STOCKS, new IntentGetMyStocks());
    	intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_GET_MY_STOCKS_WITH_EARNINGS, new IntentGetMyStocksWithEarnings());

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
		intentInterfaceByIntentNameMap.put("AMAZON.StopIntent", new IntentStopCancel());
		intentInterfaceByIntentNameMap.put("AMAZON.CancelIntent", new IntentStopCancel());
    }
        
    
    public static void init() {
    	CloudWatchLogger.init();
    }
    
	
    @Override
	public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
    }


	@Override
	public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
		SpeechletResponse response = LaunchSanjoysHelper.onLaunch(session);
		return response;
	}

	
	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		try {
			String intentName = request.getIntent().getName();
			if (intentName.equals("AMAZON.YesIntent") || intentName.equals("AMAZON.NoIntent")) {
				String lastIntentName = (String) session.getAttribute(InterfaceIntent.ATTR_LAST_INTENT);
				if (lastIntentName != null) {
					InterfaceIntent interfaceIntent = intentInterfaceByIntentNameMap.get(lastIntentName);
					if (interfaceIntent != null) {
						return interfaceIntent.onIntent(request, session);
					}				
				}
			}
			if (intentName.equals("AMAZON.HelpIntent")) {
				return LaunchSanjoysHelper.onLaunch(session);
			}
	
			InterfaceIntent interfaceIntent = intentInterfaceByIntentNameMap.get(intentName);
			if (interfaceIntent != null) {
				return interfaceIntent.onIntent(request, session);
			}
	
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText("Finance Helper has no idea what to do with this intent: " + intentName);
			return SpeechletResponse.newTellResponse(outputSpeech);
		}
		catch (SpeechletException e) {
			logger.log(Level.SEVERE, "SpeechletException in EarningsSpeechlet.onIntent()", e);
			throw e;
		}
		catch (Throwable e) {
			logger.log(Level.SEVERE, "Exception in EarningsSpeechlet.onIntent()", e);
			throw new SpeechletException(e);
		}
	}

	
	@Override
	public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
	}
}
