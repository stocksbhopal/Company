package com.sanjoyghosh.company.earnings.lambda;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.sanjoyghosh.company.earnings.intent.IntentAddCompany;
import com.sanjoyghosh.company.earnings.intent.IntentGetStockPrice;
import com.sanjoyghosh.company.earnings.intent.IntentListCompanies;
import com.sanjoyghosh.company.earnings.intent.IntentListEarnings;
import com.sanjoyghosh.company.earnings.intent.IntentMyStocksMovers;
import com.sanjoyghosh.company.earnings.intent.IntentMyStocksStatus;
import com.sanjoyghosh.company.earnings.intent.IntentUpdatePrices;
import com.sanjoyghosh.company.earnings.intent.InterfaceIntent;
import com.sanjoyghosh.company.earnings.utils.CompanyFacts;
import com.sanjoyghosh.company.earnings.utils.CompanyFactsUtils;

public class EarningsSpeechlet implements Speechlet  {

    private static final Logger log = LoggerFactory.getLogger(EarningsSpeechlet.class);
    
    private static final Map<String, InterfaceIntent> intentInterfaceByIntentNameMap = new HashMap<>();
    static {
    	CompanyFacts cf = CompanyFactsUtils.getCompanyFactsForSymbol("AMZN");
    	System.out.println("CompanyFactUtils initialized + " + cf.getFullName());
    	
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_GET_STOCK_PRICE, new IntentGetStockPrice());
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_GET_STOCK_PRICE_SPELL_ONE, new IntentGetStockPrice());
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_GET_STOCK_PRICE_SPELL_TWO, new IntentGetStockPrice());
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_GET_STOCK_PRICE_SPELL_THREE, new IntentGetStockPrice());
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_GET_STOCK_PRICE_SPELL_FOUR, new IntentGetStockPrice());
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_GET_STOCK_PRICE_SPELL_FIVE, new IntentGetStockPrice());
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_GET_STOCK_PRICE_SPELL_SIX, new IntentGetStockPrice());
		
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_ADD_COMPANY, new IntentAddCompany());
		
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_LIST_COMPANIES, new IntentListCompanies());
		
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_LIST_EARNINGS_MY_BY, new IntentListEarnings());
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_LIST_INDEX_EARNINGS, new IntentListEarnings());
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_LIST_EARNINGS_NEXT, new IntentListEarnings());
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_LIST_INDEX_EARNINGS_NEXT, new IntentListEarnings());
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_LIST_EARNINGS_MY_NEXT, new IntentListEarnings());
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_LIST_INDEX_EARNINGS_MY_NEXT, new IntentListEarnings());
		
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_MY_STOCKS_STATUS, new IntentMyStocksStatus());
		
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_MY_STOCKS_GAINERS, new IntentMyStocksMovers());
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_MY_STOCKS_LOSERS, new IntentMyStocksMovers());
		
		intentInterfaceByIntentNameMap.put(InterfaceIntent.INTENT_UPDATE_PRICES, new IntentUpdatePrices());
    }
        
	
    @Override
	public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
		// TODO Auto-generated method stub
	}


	@Override
	public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		String intentName = request.getIntent().getName();
		log.info("Intent: " + intentName);

		if (intentName.equals("AMAZON.YesIntent")) {
			String lastIntentName = (String) session.getAttribute(InterfaceIntent.ATTR_LAST_INTENT);
			if (lastIntentName.equals(InterfaceIntent.INTENT_ADD_COMPANY)) {
				return new IntentAddCompany().onIntent(request, session);
			}
		}

		InterfaceIntent interfaceIntent = intentInterfaceByIntentNameMap.get(intentName);
		if (interfaceIntent != null) {
			return interfaceIntent.onIntent(request, session);
		}

		log.error("Unknown Intent Name: " + intentName);
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("Sanjoy has no idea what to do with this intent: " + intentName);
		return SpeechletResponse.newTellResponse(outputSpeech);
	}

	
	@Override
	public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
		// TODO Auto-generated method stub	
	}
}
