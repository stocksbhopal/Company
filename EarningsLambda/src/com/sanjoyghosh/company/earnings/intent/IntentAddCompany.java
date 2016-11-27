package com.sanjoyghosh.company.earnings.intent;

import javax.persistence.EntityManager;

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
import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.earnings.utils.CompanyFacts;
import com.sanjoyghosh.company.earnings.utils.CompanyFactsUtils;

public class IntentAddCompany implements InterfaceIntent {

    private static final Logger log = LoggerFactory.getLogger(IntentAddCompany.class);

    
    private SpeechletResponse confirmIntent(String company, Session session) throws SpeechletException {
		session.setAttribute(ATTR_LAST_INTENT, INTENT_ADD_COMPANY);
		session.setAttribute(ATTR_COMPANY, company);

		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("Add " + company + " to My Stocks, right?");
		
		Reprompt reprompt = new Reprompt();
		PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
		repromptSpeech.setText("You want to add " + company + ", right?");
		reprompt.setOutputSpeech(repromptSpeech);
		
		return SpeechletResponse.newAskResponse(outputSpeech, reprompt);		
    }
    
    
    private SpeechletResponse addCompany(String company, Session session) {
    	String userId = session.getUser().getUserId();
    	CompanyFacts cf = CompanyFactsUtils.getCompanyFactsForName(company);
    	
    	EntityManager em = JPAHelper.getEntityManager();
    	Company comp = CompanyUtils.fetchCompanyBySymbol(em, cf.getSymbol().toUpperCase());
    	
		DynamoDB db = new DynamoDB(new AmazonDynamoDBClient());
		Table myStocks = db.getTable(DYNDB_TABLE);
		myStocks.putItem(new Item().withPrimaryKey(DYNDB_COL_USER_ID, userId, DYNDB_COL_COMPANY, company));

		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("Added " + company + " and " + comp.getName());
		return SpeechletResponse.newTellResponse(outputSpeech);		    	
    }
    
    
	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		String intentName = request.getIntent().getName();
		String company = intentName.equals(INTENT_ADD_COMPANY) ?
			request.getIntent().getSlot(SLOT_COMPANY).getValue() :
			(String) session.getAttribute(ATTR_COMPANY);
		company = company.toLowerCase();
		
		log.info(INTENT_ADD_COMPANY + " invoked for company: " + company + ", with Intent: " + intentName);
		
		if (intentName.equals(INTENT_ADD_COMPANY)) {
			return confirmIntent(company, session);
		}
		return addCompany(company, session);		
	}
}
