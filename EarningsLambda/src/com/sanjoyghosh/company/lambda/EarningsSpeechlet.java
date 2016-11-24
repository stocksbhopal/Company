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
import com.sanjoyghosh.company.source.nasdaq.NasdaqRealtimeQuote;
import com.sanjoyghosh.company.source.nasdaq.NasdaqRealtimeQuoteReader;

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
			String company = request.getIntent().getSlot("company").getValue();
			log.error("Company: " + company);
			
			String symbol = "amzn";
			if (company.equals("agilent")) symbol = "a";
			else if (company.equals("adobe")) symbol = "adbe";
			else if (company.equals("amazon")) symbol = "amzn";
			else if (company.equals("ambarella")) symbol = "amba";		// Doesn't work
			else if (company.equals("american towers")) symbol = "amt";
			else if (company.equals("analog devices")) symbol = "adi";
			else if (company.equals("apple")) symbol = "aapl";
			else if (company.equals("applied materials")) symbol = "amat";
			else if (company.equals("arm holdings")) symbol = "armh";
			else if (company.equals("google")) symbol = "goog";
			else if (company.equals("netflix")) symbol = "nflx";
			else if (company.equals("nvidia")) symbol = "nvda";  // Doesn't ASR well.
			else if (company.equals("yahoo")) symbol = "yhoo";
			else company = "amazon";
			
			NasdaqRealtimeQuote quote = NasdaqRealtimeQuoteReader.fetchNasdaqStockSummary(symbol);
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			log.error(request.toString());
			outputSpeech.setText("Price of " + company + " is " + (quote == null ? "INFINITE" : String.valueOf(quote.getPrice())));
			
			return SpeechletResponse.newTellResponse(outputSpeech);
		} 
		catch (IOException e) {
			e.printStackTrace();
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
