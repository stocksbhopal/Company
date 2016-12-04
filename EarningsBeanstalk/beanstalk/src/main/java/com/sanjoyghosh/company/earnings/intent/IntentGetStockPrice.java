package com.sanjoyghosh.company.earnings.intent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.sanjoyghosh.company.earnings.reader.NasdaqRealtimeQuote;
import com.sanjoyghosh.company.earnings.reader.NasdaqRealtimeQuoteReader;
import com.sanjoyghosh.company.earnings.utils.CompanyFacts;
import com.sanjoyghosh.company.earnings.utils.CompanyFactsUtils;
import com.sanjoyghosh.company.earnings.utils.StringUtils;

public class IntentGetStockPrice implements InterfaceIntent {

    private static final Logger log = LoggerFactory.getLogger(IntentGetStockPrice.class);

	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		String company = request.getIntent().getSlot(SLOT_COMPANY).getValue();
		log.info(INTENT_GET_STOCK_PRICE + " invoked for company: " + company);
		
		String error = null;
		try {
			CompanyFacts cf = CompanyFactsUtils.getCompanyFactsForName(company);
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
				error = INTENT_GET_STOCK_PRICE + " found no company named " + company;
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
