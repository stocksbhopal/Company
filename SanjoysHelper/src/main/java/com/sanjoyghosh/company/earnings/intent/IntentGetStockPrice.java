package com.sanjoyghosh.company.earnings.intent;

import java.util.ArrayList;
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
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.logs.AWSLogsAsync;
import com.amazonaws.services.logs.AWSLogsAsyncClient;
import com.amazonaws.services.logs.AWSLogsAsyncClientBuilder;
import com.amazonaws.services.logs.model.InputLogEvent;
import com.amazonaws.services.logs.model.PutLogEventsRequest;
import com.sanjoyghosh.company.db.CompanyJPA;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.source.nasdaq.NasdaqRealtimeQuote;
import com.sanjoyghosh.company.source.nasdaq.NasdaqRealtimeQuoteReader;
import com.sanjoyghosh.company.utils.CloudWatchLogger;
import com.sanjoyghosh.company.utils.LoggerUtils;
import com.sanjoyghosh.company.utils.StringUtils;

public class IntentGetStockPrice implements InterfaceIntent {

    private static final Logger logger = Logger.getLogger(IntentGetStockPrice.class.getPackage().getName());

    
    private SpeechletResponse respondWithPrice(CompanyOrSymbol companyOrSymbol, Session session) {
    	String error = "";
		try {
			Company company = null;
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

					InputLogEvent logEvent = new InputLogEvent().withMessage(text).withTimestamp(new Date().getTime());
					List<InputLogEvent> logEventList = new ArrayList<>();
					logEventList.add(logEvent);
					
					PutLogEventsRequest logRequest = new PutLogEventsRequest();
					logRequest.setLogStreamName("GetStockPrice");
					logRequest.setLogGroupName("FinanceHelper");
					logRequest.setLogEvents(logEventList);
					
					CloudWatchLogger.getInstance().getCloudWatchLoggerAsync().putLogEvents(logRequest);
					
					PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
					outputSpeech.setText(text);
					return SpeechletResponse.newTellResponse(outputSpeech);
				}
				else {
					error = INTENT_GET_STOCK_PRICE + " found no quote for company named " + company.getSpeechName();
				}
			}
			else {
				error = INTENT_GET_STOCK_PRICE + " found no company or symbol:" + companyOrSymbol;
			}
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, LoggerUtils.makeLogString(session, INTENT_GET_STOCK_PRICE + " exception in respondWithPrice()"), e);
		}
		
		logger.info(LoggerUtils.makeLogString(session, error));
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(error);
		return SpeechletResponse.newTellResponse(outputSpeech);		    	
    }
    

	private SpeechletResponse respondWithQuestion(Session session) {
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("Price of what company?  Tell me the name or symbol.");

		Reprompt reprompt = new Reprompt();
		PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
		repromptSpeech.setText("Sorry, need the name or symbol to get the price.");
		reprompt.setOutputSpeech(repromptSpeech);
		
		logger.info(LoggerUtils.makeLogString(session, INTENT_GET_STOCK_PRICE + " user did not provide name of company."));
		return SpeechletResponse.newAskResponse(outputSpeech, reprompt);	    	
	}

	
	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		CompanyOrSymbol companyOrSymbol = IntentUtils.getCompanyOrSymbol(request);		
		if (companyOrSymbol == null || companyOrSymbol.isEmpty()) {
			return respondWithQuestion(session);
		}
		else {
			return respondWithPrice(companyOrSymbol, session);
		}
	}
}
