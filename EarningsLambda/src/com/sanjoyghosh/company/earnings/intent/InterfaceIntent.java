package com.sanjoyghosh.company.earnings.intent;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;

public interface InterfaceIntent {

    public static final String ATTR_LAST_INTENT = "ATTR_LAST_INTENT";
    public static final String ATTR_COMPANY = "ATTR_COMPANY";

    public static final String INTENT_ADD_COMPANY = "intentAddCompany";
    
    
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException;
}
