package com.sanjoyghosh.company.earnings.intent;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazonaws.services.dynamodbv2.document.Item;

public class IntentListCompanies implements InterfaceIntent {

    private static final Logger log = LoggerFactory.getLogger(IntentListCompanies.class);

    
	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		String companyListStr = "";
		String userId = session.getUser().getUserId();
        Iterator<Item> iterator = DynamoDBUtils.queryMyStocksByUserId(userId);
        
        Item item = null;
        try {
            while (iterator.hasNext()) {
                item = iterator.next();
                String company = item.getString(DYNDB_COL_FULL_NAME);
                companyListStr += company + ", ";
            }

        } 
        catch (Exception e) {
        	String error = "Could not retrieve the companies in My Stocks";
        	log.error(error, e);
        	
    		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
    		outputSpeech.setText(error);
    		return SpeechletResponse.newTellResponse(outputSpeech);		    	
        }
		
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("The companies in My Stocks are " + companyListStr);
		return SpeechletResponse.newTellResponse(outputSpeech);		    	
	}
}
