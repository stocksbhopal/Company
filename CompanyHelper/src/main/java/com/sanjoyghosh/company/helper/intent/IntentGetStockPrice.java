package com.sanjoyghosh.company.helper.intent;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.sanjoyghosh.company.db.CompanyJPA;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.CompanyNamePrefix;
import com.sanjoyghosh.company.source.nasdaq.NasdaqIndexes;
import com.sanjoyghosh.company.source.nasdaq.NasdaqIndexesReader;
import com.sanjoyghosh.company.source.nasdaq.NasdaqRealtimeQuote;
import com.sanjoyghosh.company.source.nasdaq.NasdaqRealtimeQuoteReader;
import com.sanjoyghosh.company.utils.StringUtils;

public class IntentGetStockPrice implements InterfaceIntent {
	
    private static final Logger logger = Logger.getLogger(IntentGetStockPrice.class.getName());


	public static final int RESULT_INCOMPLETE = 1;
	public static final int RESULT_SUCCESS = 0;
	public static final int RESULT_ERROR_NO_QUOTE = -1;
	public static final int RESULT_ERROR_NO_COMPANY = -2;
	public static final int RESULT_ERROR_EXCEPTION = -3;
	
    
    private SpeechletResponse makeTellResponse(NasdaqRealtimeQuote quote, CompanyNamePrefix companyNamePrefix, Company company, AllSlotValues slotValues, 
    	IntentRequest request, Session session) {
    	
		String price = StringUtils.toStringWith2DecimalPlaces(quote.getPrice());
		String priceChange = StringUtils.toStringWith2DecimalPlaces(quote.getPriceChange());
		String priceChangePercent = StringUtils.toStringWith2DecimalPlaces(quote.getPriceChangePercent());
		String text = null;
		if (companyNamePrefix != null && companyNamePrefix.isManuallyAdded()) {
			text = "Price of " + slotValues.getCompanyOrSymbol() + ", whose registered name is " + company.getName() + ", is " + price + 
			", " + (quote.getPriceChange() > 0.00D ? "up " : "down ") + priceChange +
			", " + (quote.getPriceChange() > 0.00D ? "up " : "down ") + priceChangePercent + " percent";
		}
		else {
			text = "Price of " + company.getName() + " is " + price + 
			", " + (quote.getPriceChange() > 0.00D ? "up " : "down ") + priceChange +
			", " + (quote.getPriceChange() > 0.00D ? "up " : "down ") + priceChangePercent + " percent";
			
		}

		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(text);
		return SpeechletResponse.newTellResponse(outputSpeech);
    }
    
    
    private SpeechletResponse respondWithPrice(EntityManager em, IntentResult result, IntentRequest request, Session session) {
    	String error = "";
    	Exception exception = null;
		Company company = null;
		CompanyNamePrefix companyNamePrefix = null;
		String intentName = result.getName();
		AllSlotValues slotValues = result.getSlotValues();
		
		try {			
			company = CompanyJPA.fetchCompanyByNameOrSymbol(em, result);
			if (company != null) {
				String symbol = company.getSymbol();
				result.setResponse(symbol);
				if (symbol.equals("DJIA") || symbol.equals("IXIC") || symbol.equals("GSPC")) {
					NasdaqIndexes indexes = NasdaqIndexesReader.readNasdaqIndexes();
					if (indexes != null) {
						NasdaqRealtimeQuote quote = symbol.equals("DJIA") ? indexes.getDjiaQuote() : symbol.equals("IXIC") ? indexes.getIxicQuote() : indexes.getGspcQuote();
						result.setResult(RESULT_SUCCESS);
						return makeTellResponse(quote, null, company, slotValues, request, session);
					}
					else {
						result.setResult(RESULT_ERROR_NO_QUOTE);
						error = intentName + " found no quote for index named " + company.getName();						
					}
				}
				else {
					NasdaqRealtimeQuote quote = NasdaqRealtimeQuoteReader.fetchNasdaqStockSummary(company.getSymbol());
					if (quote != null) {
						result.setResult(RESULT_SUCCESS);
						return makeTellResponse(quote, companyNamePrefix, company, slotValues, request, session);
					}
					else {
						result.setResult(RESULT_ERROR_NO_QUOTE);
						error = intentName + " found no quote for company named " + company.getName();
					}
				}
			}
			else {
				result.setResult(RESULT_ERROR_NO_COMPANY);
				error = intentName + " found no company or symbol:" + slotValues;
			}
		}
		catch (Exception e) {
			exception = e;
			result.setResult(RESULT_ERROR_EXCEPTION);
		}
		logger.log(Level.SEVERE, error, exception);

		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(error);
		return SpeechletResponse.newTellResponse(outputSpeech);		    	
    }
    
	
	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session, IntentResult result) throws SpeechletException {
		EntityManager em = null;
		try {
			em = JPAHelper.getEntityManager();
			return respondWithPrice(em, result, request, session);
		}
		finally {
			if (em != null) {
				em.close();
			}
		}
	}
}
