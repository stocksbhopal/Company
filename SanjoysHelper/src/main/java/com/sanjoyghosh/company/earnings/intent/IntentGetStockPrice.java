package com.sanjoyghosh.company.earnings.intent;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.sanjoyghosh.company.cloudwatch.logs.CloudWatchLogger;
import com.sanjoyghosh.company.cloudwatch.logs.CloudWatchLoggerIntentResult;
import com.sanjoyghosh.company.db.CompanyJPA;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.source.nasdaq.NasdaqRealtimeQuote;
import com.sanjoyghosh.company.source.nasdaq.NasdaqRealtimeQuoteReader;
import com.sanjoyghosh.company.utils.KeyValuePair;
import com.sanjoyghosh.company.utils.LoggerUtils;
import com.sanjoyghosh.company.utils.StringUtils;

public class IntentGetStockPrice implements InterfaceIntent {

	public static final int RESULT_INCOMPLETE = 1;
	public static final int RESULT_SUCCESS = 0;
	public static final int RESULT_ERROR_NO_QUOTE = -1;
	public static final int RESULT_ERROR_NO_COMPANY = -2;
	public static final int RESULT_ERROR_EXCEPTION = -3;
	
	
    private static final Logger logger = Logger.getLogger(IntentGetStockPrice.class.getPackage().getName());

    
    private CloudWatchLoggerIntentResult makeCloudWatchLoggerResult(
    	String alexaUserId, String intentName, int result, String response, CompanyOrSymbol companyOrSymbol) {
    	
    	List<KeyValuePair> inputs = (companyOrSymbol != null) ? companyOrSymbol.toKeyValuePairList() : null;
    	CloudWatchLoggerIntentResult loggerResult = new CloudWatchLoggerIntentResult(alexaUserId, intentName, result, response, inputs, new Date());
    	return loggerResult;
    }
    
    
    private SpeechletResponse respondWithPrice(CompanyOrSymbol companyOrSymbol, IntentRequest request, Session session) {
    	String error = "";
		Company company = null;
		CloudWatchLoggerIntentResult loggerResult = null;
		try {			
			List<Company> companyList = CompanyJPA.fetchCompanyListByNamePrefix(companyOrSymbol.getCompanyOrSymbol());
			if (companyList.size() > 1) {
				logger.log(Level.SEVERE, LoggerUtils.makeLogString(session,  INTENT_GET_STOCK_PRICE + "Found mutiple companies for: " + companyOrSymbol.getCompanyOrSymbol()));
			}
			company = companyList.size() > 0 ? companyList.get(0) : null;
			
			company = company != null ? company : CompanyJPA.fetchCompanyBySymbol(companyOrSymbol.getCompanyOrSymbol());
			company = company != null ? company : CompanyJPA.fetchCompanyBySymbol(companyOrSymbol.getSymbol());
			if (company != null) {
				NasdaqRealtimeQuote quote = NasdaqRealtimeQuoteReader.fetchNasdaqStockSummary(company.getSymbol());
				if (quote != null) {
					String price = StringUtils.toStringWith2DecimalPlaces(quote.getPrice());
					String priceChange = StringUtils.toStringWith2DecimalPlaces(quote.getPriceChange());
					String priceChangePercent = StringUtils.toStringWith2DecimalPlaces(quote.getPriceChangePercent());
					String text = "Price of " + company.getSpeechName() + " is " + price + 
						", " + (quote.getPriceChange() > 0.00D ? "up " : "down ") + priceChange +
						", " + (quote.getPriceChange() > 0.00D ? "up " : "down ") + priceChangePercent + " percent";
					logger.info(LoggerUtils.makeLogString(session, INTENT_GET_STOCK_PRICE + " found company:" + company.getSymbol().toUpperCase() + ", for user input:" + companyOrSymbol));

					loggerResult = makeCloudWatchLoggerResult(
						session.getUser().getUserId(), request.getIntent().getName(), RESULT_SUCCESS, company.getSymbol(), companyOrSymbol);
					CloudWatchLogger.getInstance().addLogEvent(loggerResult);
							
					PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
					outputSpeech.setText(text);
					return SpeechletResponse.newTellResponse(outputSpeech);
				}
				else {
					loggerResult = makeCloudWatchLoggerResult(
						session.getUser().getUserId(), request.getIntent().getName(), RESULT_ERROR_NO_QUOTE, company.getSymbol(), companyOrSymbol);
					error = INTENT_GET_STOCK_PRICE + " found no quote for company named " + company.getSpeechName();
				}
			}
			else {
				loggerResult = makeCloudWatchLoggerResult(
					session.getUser().getUserId(), request.getIntent().getName(), RESULT_ERROR_NO_COMPANY, null, companyOrSymbol);
				error = INTENT_GET_STOCK_PRICE + " found no company or symbol:" + companyOrSymbol;
			}
		}
		catch (Exception e) {
			loggerResult = makeCloudWatchLoggerResult(
				session.getUser().getUserId(), request.getIntent().getName(), RESULT_ERROR_EXCEPTION, (company == null ? null : company.getSymbol()), companyOrSymbol);
			logger.log(Level.SEVERE, LoggerUtils.makeLogString(session, INTENT_GET_STOCK_PRICE + " exception in respondWithPrice()"), e);
		}
		
		CloudWatchLogger.getInstance().addLogEvent(loggerResult);
		logger.info(LoggerUtils.makeLogString(session, error));
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
	   	
		CloudWatchLoggerIntentResult loggerResult = new CloudWatchLoggerIntentResult(
			session.getUser().getUserId(), request.getIntent().getName(), RESULT_INCOMPLETE, null, null, new Date());
		CloudWatchLogger.getInstance().addLogEvent(loggerResult);

		logger.info(LoggerUtils.makeLogString(session, INTENT_GET_STOCK_PRICE + " user did not provide name of company."));
		return SpeechletResponse.newAskResponse(outputSpeech, reprompt);	    	
	}

	
	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		CompanyOrSymbol companyOrSymbol = IntentUtils.getCompanyOrSymbol(request);		
		if (companyOrSymbol == null || companyOrSymbol.isEmpty()) {
			return respondWithQuestion(request, session);
		}
		else {
			return respondWithPrice(companyOrSymbol, request, session);
		}
	}
}
