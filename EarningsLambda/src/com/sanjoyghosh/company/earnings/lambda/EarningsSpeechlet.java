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

public class EarningsSpeechlet implements Speechlet {

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
		if (intentName.equals("GetStockPrice")) {
			return new IntentGetStockPrice().onIntent(request, session);
		}		
		if (intentName.equals("AddCompany")) {
			return new IntentAddCompany().onIntent(request, session);
		}		
		
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		log.error(request.toString());
		System.err.println(request.toString());
		outputSpeech.setText("Really this is Yes You will have great earnings soon like tomorrow.");
		return SpeechletResponse.newTellResponse(outputSpeech);
	}

	
	@Override
	public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
		// TODO Auto-generated method stub
		
	}
}
