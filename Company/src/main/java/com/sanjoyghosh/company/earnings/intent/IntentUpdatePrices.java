package com.sanjoyghosh.company.earnings.intent;

import java.util.concurrent.atomic.AtomicBoolean;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.sanjoyghosh.company.source.nasdaq.NasdaqPriceUpdater;

public class IntentUpdatePrices implements InterfaceIntent {

	private static AtomicBoolean isUpdatingPrices = new AtomicBoolean(false);
	
	
	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		if (isUpdatingPrices.compareAndSet(false, true)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					NasdaqPriceUpdater.updatePrices();
					isUpdatingPrices.set(false);
				}
			}, "PricesUpdaterThread").start();
		}
        
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("No problem, I am updating the prices.  It will be a couple minutes.");
		return SpeechletResponse.newTellResponse(outputSpeech);
	}
}
