package com.sanjoyghosh.company.earnings.lambda;

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
import com.sanjoyghosh.company.earnings.intent.IntentMyStocksStatus;
import com.sanjoyghosh.company.earnings.intent.InterfaceIntent;

public class EarningsSpeechlet implements Speechlet  {

    private static final Logger log = LoggerFactory.getLogger(EarningsSpeechlet.class);
        
	
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

		if (intentName.equals(InterfaceIntent.INTENT_GET_STOCK_PRICE)) {
			return new IntentGetStockPrice().onIntent(request, session);
		}		
		if (intentName.equals(InterfaceIntent.INTENT_ADD_COMPANY)) {
			return new IntentAddCompany().onIntent(request, session);
		}		
		if (intentName.equals(InterfaceIntent.INTENT_LIST_COMPANIES)) {
			return new IntentListCompanies().onIntent(request, session);
		}
		if (intentName.equals(InterfaceIntent.INTENT_LIST_EARNINGS_MY_BY)) {
			return new IntentListEarnings().onIntent(request, session);
		}
		if (intentName.equals(InterfaceIntent.INTENT_LIST_INDEX_EARNINGS)) {
			return new IntentListEarnings().onIntent(request, session);
		}
		if (intentName.equals(InterfaceIntent.INTENT_LIST_EARNINGS_NEXT)) {
			return new IntentListEarnings().onIntent(request, session);
		}
		if (intentName.equals(InterfaceIntent.INTENT_LIST_INDEX_EARNINGS_NEXT)) {
			return new IntentListEarnings().onIntent(request, session);
		}
		if (intentName.equals(InterfaceIntent.INTENT_LIST_EARNINGS_MY_NEXT)) {
			return new IntentListEarnings().onIntent(request, session);
		}
		if (intentName.equals(InterfaceIntent.INTENT_LIST_INDEX_EARNINGS_MY_NEXT)) {
			return new IntentListEarnings().onIntent(request, session);
		}
		if (intentName.equals(InterfaceIntent.INTENT_MY_STOCKS_STATUS)) {
			return new IntentMyStocksStatus().onIntent(request, session);
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
