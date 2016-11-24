package com.sanjoyghosh.company.earnings.intent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.sanjoyghosh.company.earnings.utils.CompanyFacts;
import com.sanjoyghosh.company.earnings.utils.CompanyFactsUtils;
import com.sanjoyghosh.company.source.nasdaq.NasdaqRealtimeQuote;
import com.sanjoyghosh.company.source.nasdaq.NasdaqRealtimeQuoteReader;

public class IntentGetStockPrice implements InterfaceIntent {

    private static final Logger log = LoggerFactory.getLogger(IntentGetStockPrice.class);

	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		String company = request.getIntent().getSlot("company").getValue();
		log.info("GetStockPrice invoked for company: " + company);
		
		String error = null;
		try {
			CompanyFacts cf = CompanyFactsUtils.getCompanyFactsForName(company);
			if (cf != null) {
				NasdaqRealtimeQuote quote = NasdaqRealtimeQuoteReader.fetchNasdaqStockSummary(cf.getSymbol());
				if (quote != null) {
					PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
					outputSpeech.setText("Price of " + company + " is " + quote.getPrice());
					return SpeechletResponse.newTellResponse(outputSpeech);
				}
				else {
					error = "GetStockPrice found no quote for company named " + company;
				}
			}
			else {
				error = "GetStockPrice found no company named " + company;
			}
		}
		catch (Exception e) {
			log.error(error, e);
		}
		
		log.error(error);
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(error);
		return SpeechletResponse.newTellResponse(outputSpeech);		
	}
}
