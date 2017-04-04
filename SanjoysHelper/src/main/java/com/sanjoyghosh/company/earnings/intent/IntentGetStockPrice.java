package com.sanjoyghosh.company.earnings.intent;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.sanjoyghosh.company.db.CompanyJPA;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.CompanyNamePrefix;
import com.sanjoyghosh.company.logs.CloudWatchLogger;
import com.sanjoyghosh.company.logs.CloudWatchLoggerIntentResult;
import com.sanjoyghosh.company.source.nasdaq.NasdaqIndexes;
import com.sanjoyghosh.company.source.nasdaq.NasdaqIndexesReader;
import com.sanjoyghosh.company.source.nasdaq.NasdaqRealtimeQuote;
import com.sanjoyghosh.company.source.nasdaq.NasdaqRealtimeQuoteReader;
import com.sanjoyghosh.company.utils.KeyValuePair;
import com.sanjoyghosh.company.utils.StringUtils;

public class IntentGetStockPrice implements InterfaceIntent {

	public static final int RESULT_INCOMPLETE = 1;
	public static final int RESULT_SUCCESS = 0;
	public static final int RESULT_ERROR_NO_QUOTE = -1;
	public static final int RESULT_ERROR_NO_COMPANY = -2;
	public static final int RESULT_ERROR_EXCEPTION = -3;
	
    
    private CloudWatchLoggerIntentResult makeCloudWatchLoggerResult(
    	String alexaUserId, String intentName, int result, String response, AllSlotValues slotValues) {
    	
    	List<KeyValuePair> inputs = (slotValues != null) ? slotValues.toKeyValuePairList() : null;
    	CloudWatchLoggerIntentResult loggerResult = new CloudWatchLoggerIntentResult(alexaUserId, intentName, result, response, inputs, new Date());
    	return loggerResult;
    }
    
    
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

		String intentName = request.getIntent().getName();
		String juliLoggerMessage = intentName + ": found company:" + company.getSymbol().toUpperCase() + ", for user input:" + slotValues;
		CloudWatchLoggerIntentResult loggerResult = makeCloudWatchLoggerResult(
			session.getUser().getUserId(), intentName, RESULT_SUCCESS, company.getSymbol(), slotValues);
		CloudWatchLogger.getInstance().addLogEvent(loggerResult, juliLoggerMessage);
				
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(text);
		return SpeechletResponse.newTellResponse(outputSpeech);
    }
    
    
    private SpeechletResponse respondWithPrice(EntityManager em, AllSlotValues slotValues, IntentRequest request, Session session) {
    	String error = "";
		Company company = null;
		CompanyNamePrefix companyNamePrefix = null;
		CloudWatchLoggerIntentResult loggerResult = null;
    	String intentName = request.getIntent().getName();
		try {			
			company = CompanyJPA.fetchCompanyByNameOrSymbol(em, slotValues);
			if (company != null) {
				String symbol = company.getSymbol();
				if (symbol.equals("DJIA") || symbol.equals("IXIC") || symbol.equals("GSPC")) {
					NasdaqIndexes indexes = NasdaqIndexesReader.readNasdaqIndexes();
					if (indexes != null) {
						NasdaqRealtimeQuote quote = symbol.equals("DJIA") ? indexes.getDjiaQuote() : symbol.equals("IXIC") ? indexes.getIxicQuote() : indexes.getGspcQuote();
						return makeTellResponse(quote, null, company, slotValues, request, session);
					}
					else {
						loggerResult = makeCloudWatchLoggerResult(
							session.getUser().getUserId(), request.getIntent().getName(), RESULT_ERROR_NO_QUOTE, company.getSymbol(), slotValues);
						error = intentName + " found no quote for index named " + company.getName();						
					}
				}
				else {
					NasdaqRealtimeQuote quote = NasdaqRealtimeQuoteReader.fetchNasdaqStockSummary(company.getSymbol());
					if (quote != null) {
						return makeTellResponse(quote, companyNamePrefix, company, slotValues, request, session);
					}
					else {
						loggerResult = makeCloudWatchLoggerResult(
							session.getUser().getUserId(), request.getIntent().getName(), RESULT_ERROR_NO_QUOTE, company.getSymbol(), slotValues);
						error = intentName + " found no quote for company named " + company.getName();
					}
				}
			}
			else {
				loggerResult = makeCloudWatchLoggerResult(
					session.getUser().getUserId(), request.getIntent().getName(), RESULT_ERROR_NO_COMPANY, null, slotValues);
				error = intentName + " found no company or symbol:" + slotValues;
			}
		}
		catch (Exception e) {
			loggerResult = makeCloudWatchLoggerResult(
				session.getUser().getUserId(), request.getIntent().getName(), RESULT_ERROR_EXCEPTION, (company == null ? null : company.getSymbol()), slotValues);
		}
		
		CloudWatchLogger.getInstance().addLogEvent(loggerResult, intentName + ": Exception in respondWithPrice()");
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(error);
		return SpeechletResponse.newTellResponse(outputSpeech);		    	
    }
    

	private SpeechletResponse respondWithQuestion(IntentRequest request, Session session) {
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("Price of what company?  Tell me the name or symbol.");

		Reprompt reprompt = new Reprompt();
		PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
		repromptSpeech.setText("Sorry, need the name or symbol to get the price.");
		reprompt.setOutputSpeech(repromptSpeech);
	   	
		String intentName = request.getIntent().getName();
		CloudWatchLoggerIntentResult loggerResult = new CloudWatchLoggerIntentResult(
			session.getUser().getUserId(), intentName, RESULT_INCOMPLETE, null, null, new Date());
		CloudWatchLogger.getInstance().addLogEvent(loggerResult, intentName + ": user did not provide name of company.");
		
		return SpeechletResponse.newAskResponse(outputSpeech, reprompt);	    	
	}

	
	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		EntityManager em = null;
		try {
			em = JPAHelper.getEntityManager();
			AllSlotValues slotValues = new AllSlotValues();
			if (!IntentUtils.getCompanyOrSymbol(request, session, slotValues)) {	
				return respondWithQuestion(request, session);
			}
			else {
				return respondWithPrice(em, slotValues, request, session);
			}
		}
		finally {
			if (em != null) {
				em.close();
			}
		}
	}
}
