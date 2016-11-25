package com.sanjoyghosh.company.earnings.intent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;

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
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("Added " + company);
		return SpeechletResponse.newTellResponse(outputSpeech);		    	
    }
    
    
	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		String intentName = request.getIntent().getName();
		String company = intentName.equals("AddCompany") ?
			request.getIntent().getSlot("company").getValue() :
			(String) session.getAttribute(ATTR_COMPANY);
		log.info("AddCompany invoked for company: " + company + ", with Intent: " + intentName);
		
		if (intentName.equals("AddCompany")) {
			return confirmIntent(company, session);
		}
		return addCompany(company, session);		
	}
}
