package com.sanjoyghosh.company.earnings.intent;

import org.apache.log4j.Logger;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.sanjoyghosh.company.earnings.utils.CompanyFacts;
import com.sanjoyghosh.company.earnings.utils.CompanyFactsUtils;
import com.sanjoyghosh.company.source.nasdaq.NasdaqRealtimeQuote;
import com.sanjoyghosh.company.source.nasdaq.NasdaqRealtimeQuoteReader;
import com.sanjoyghosh.company.utils.StringUtils;

public class IntentGetStockPrice implements InterfaceIntent {

    private static final Logger log = Logger.getLogger(IntentGetStockPrice.class);

    
    private String getCompanyOrSymbol(IntentRequest request) {
    	String companyOrSymbol = "";
    	Intent intent = request.getIntent();
    	if (intent.getSlot(SLOT_COMPANY) != null) {
    		companyOrSymbol = intent.getSlot(SLOT_COMPANY).getValue();
    	}
    	else {
    		if (intent.getSlot(SLOT_SPELLING_SIX) != null) {
    			companyOrSymbol = intent.getSlot(SLOT_SPELLING_SIX).getValue().substring(0, 1) + companyOrSymbol;
    		}
    		if (intent.getSlot(SLOT_SPELLING_FIVE) != null) {
    			companyOrSymbol = intent.getSlot(SLOT_SPELLING_FIVE).getValue().substring(0, 1) + companyOrSymbol;
    		}
    		if (intent.getSlot(SLOT_SPELLING_FOUR) != null) {
    			companyOrSymbol = intent.getSlot(SLOT_SPELLING_FOUR).getValue().substring(0, 1) + companyOrSymbol;
    		}
    		if (intent.getSlot(SLOT_SPELLING_THREE) != null) {
    			companyOrSymbol = intent.getSlot(SLOT_SPELLING_THREE).getValue().substring(0, 1) + companyOrSymbol;
    		}
    		if (intent.getSlot(SLOT_SPELLING_TWO) != null) {
    			companyOrSymbol = intent.getSlot(SLOT_SPELLING_TWO).getValue().substring(0, 1) + companyOrSymbol;
    		}
    		if (intent.getSlot(SLOT_SPELLING_ONE) != null) {
    			companyOrSymbol = intent.getSlot(SLOT_SPELLING_ONE).getValue().substring(0, 1) + companyOrSymbol;
    		}
    	}
		System.out.println(INTENT_GET_STOCK_PRICE + " invoked for company or symbol: " + companyOrSymbol);
		return companyOrSymbol;
    }
    
    
	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		String error = null;
		String companyOrSymbol = getCompanyOrSymbol(request);
		
		try {
			CompanyFacts cf = CompanyFactsUtils.getCompanyFactsForSymbol(companyOrSymbol);
			cf = cf != null ? cf : CompanyFactsUtils.getCompanyFactsForName(companyOrSymbol);
			if (cf != null) {
				NasdaqRealtimeQuote quote = NasdaqRealtimeQuoteReader.fetchNasdaqStockSummary(cf.getSymbol());
				if (quote != null) {
					String price = StringUtils.toStringWith2DecimalPlaces(quote.getPrice());
					String priceChange = StringUtils.toStringWith2DecimalPlaces(quote.getPriceChange());
					String priceChangePercent = StringUtils.toStringWith2DecimalPlaces(quote.getPriceChangePercent());
					String text = "Price of " + cf.getFullName() + " is " + price + 
						", " + (quote.getPriceChange() > 0.00D ? "up " : "down ") + priceChange +
						", " + (quote.getPriceChange() > 0.00D ? "up " : "down ") + priceChangePercent + " percent";
					
					PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
					outputSpeech.setText(text);
					return SpeechletResponse.newTellResponse(outputSpeech);
				}
				else {
					error = INTENT_GET_STOCK_PRICE + " found no quote for company named " + cf.getFullName();
				}
			}
			else {
				error = INTENT_GET_STOCK_PRICE + " found no company named " + companyOrSymbol;
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
