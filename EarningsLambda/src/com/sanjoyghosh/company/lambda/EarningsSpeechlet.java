package com.sanjoyghosh.company.lambda;

import java.io.IOException;

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
import com.sanjoyghosh.company.source.yahoo.YahooStockSummary;
import com.sanjoyghosh.company.source.yahoo.YahooStockSummaryPage;

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
		try {
			YahooStockSummary summary = YahooStockSummaryPage.fetchYahooStockSummary("YHOO");
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			log.error(request.toString());
			outputSpeech.setText("Price of Yahoo is " + summary.getDayRangeHigh());
			return SpeechletResponse.newTellResponse(outputSpeech);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		log.error(request.toString());
		outputSpeech.setText("Really this is Yes You will have great earnings soon like tomorrow.");
		return SpeechletResponse.newTellResponse(outputSpeech);
	}

	@Override
	public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
		// TODO Auto-generated method stub
		
	}
}
