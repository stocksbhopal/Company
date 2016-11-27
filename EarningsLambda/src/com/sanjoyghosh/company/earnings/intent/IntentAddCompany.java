package com.sanjoyghosh.company.earnings.intent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.sanjoyghosh.company.earnings.utils.CompanyFacts;
import com.sanjoyghosh.company.earnings.utils.CompanyFactsUtils;

public class IntentAddCompany implements InterfaceIntent {

    private static final Logger log = LoggerFactory.getLogger(IntentAddCompany.class);

    
    private SpeechletResponse confirmIntent(String company, Session session) throws SpeechletException {
    	CompanyFacts cf = CompanyFactsUtils.getCompanyFactsForName(company);
    	if (cf == null) {
    		String error = InterfaceIntent.INTENT_ADD_COMPANY + " did not find a company named: " + company;
    		log.error(error);
    		
    		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
    		outputSpeech.setText(error);
    		return SpeechletResponse.newTellResponse(outputSpeech);		    	
    	}
    	
		session.setAttribute(ATTR_LAST_INTENT, INTENT_ADD_COMPANY);
		session.setAttribute(ATTR_SYMBOL, cf.getSymbol());

		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("Add " + cf.getFullName() + " to My Stocks, right?");
		
		Reprompt reprompt = new Reprompt();
		PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
		repromptSpeech.setText("You want to add " + cf.getFullName() + ", right?");
		reprompt.setOutputSpeech(repromptSpeech);
		
		return SpeechletResponse.newAskResponse(outputSpeech, reprompt);		
    }
    
    
    private SpeechletResponse addCompany(String symbol, Session session) {
    	String userId = session.getUser().getUserId();
    	CompanyFacts cf = CompanyFactsUtils.getCompanyFactsForSymbol(symbol);
    	
		DynamoDB db = new DynamoDB(new AmazonDynamoDBClient());
		Table myStocks = db.getTable(DYNDB_TABLE_MY_STOCKS);
		myStocks.putItem(new Item().withPrimaryKey(DYNDB_COL_USER_ID, userId, DYNDB_COL_SYMBOL, symbol).withString(DYNDB_COL_FULL_NAME, cf.getFullName()));

		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("Added " + cf.getFullName() + " to My Stocks");
		return SpeechletResponse.newTellResponse(outputSpeech);		    	
    }
    
    
	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		String intentName = request.getIntent().getName();		
		if (intentName.equals(INTENT_ADD_COMPANY)) {
			String company = request.getIntent().getSlot(SLOT_COMPANY).getValue().toLowerCase();
			return confirmIntent(company, session);
		}
		
		String symbol = (String) session.getAttribute(ATTR_SYMBOL);
		symbol = symbol.toLowerCase();
		return addCompany(symbol, session);		
	}
}
