package com.sanjoyghosh.company.earnings.intent;

import org.apache.log4j.Logger;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.sanjoyghosh.company.earnings.utils.CompanyFacts;
import com.sanjoyghosh.company.earnings.utils.CompanyFactsUtils;
import com.sanjoyghosh.company.source.nasdaq.NasdaqRealtimeQuote;
import com.sanjoyghosh.company.source.nasdaq.NasdaqRealtimeQuoteReader;
import com.sanjoyghosh.company.utils.StringUtils;

public class IntentGetStockPrice implements InterfaceIntent {

    private static final Logger log = Logger.getLogger(IntentGetStockPrice.class);

    
    private SpeechletResponse respondWithPrice(CompanyOrSymbol companyOrSymbol) {
    	String error = "";
		try {
			CompanyFacts cf = CompanyFactsUtils.getCompanyFactsForName(companyOrSymbol.getCompanyOrSymbol());
			cf = cf != null ? cf : CompanyFactsUtils.getCompanyFactsForSymbol(companyOrSymbol.getCompanyOrSymbol());
			cf = cf != null ? cf : CompanyFactsUtils.getCompanyFactsForSymbol(companyOrSymbol.getSymbol());
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
				error = INTENT_GET_STOCK_PRICE + " found no company or symbol " + companyOrSymbol;
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
    

	private SpeechletResponse respondWithQuestion() {
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("Price of what company?  Tell me the name or symbol.");

		Reprompt reprompt = new Reprompt();
		PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
		repromptSpeech.setText("Sorry, need the name or symbol to get the price.");
		reprompt.setOutputSpeech(repromptSpeech);
		
		return SpeechletResponse.newAskResponse(outputSpeech, reprompt);	    	
	}

	
	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		CompanyOrSymbol companyOrSymbol = IntentUtils.getCompanyOrSymbol(request);		
		if (companyOrSymbol == null || companyOrSymbol.isEmpty()) {
			return respondWithQuestion();
		}
		else {
			return respondWithPrice(companyOrSymbol);
		}
	}
}
