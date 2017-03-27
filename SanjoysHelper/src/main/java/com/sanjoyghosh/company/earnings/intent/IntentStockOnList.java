package com.sanjoyghosh.company.earnings.intent;

import java.util.logging.Logger;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.sanjoyghosh.company.utils.LoggerUtils;

public class IntentStockOnList implements InterfaceIntent {

    private static final Logger logger = Logger.getLogger(IntentStockOnList.class.getName());

    
    @Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
    	AllSlotValues companyOrSymbol = IntentUtils.getCompanyOrSymbol(request);
    	Double quantity = IntentUtils.getQuantity(request);
    	
    	String quantityStr = quantity == null ? "null" : String.valueOf(quantity);
    	String companyStr = companyOrSymbol.toString();
		logger.info(LoggerUtils.makeLogString(session, request.getIntent().getName() + ": quantity:" + quantityStr + ", company:" + companyStr));

		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("Adding " + quantityStr + " shares of " + companyStr);
		return SpeechletResponse.newTellResponse(outputSpeech);
	}
}
