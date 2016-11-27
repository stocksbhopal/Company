package com.sanjoyghosh.company.earnings.intent;

import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;

public class IntentListCompanies implements InterfaceIntent {

    private static final Logger log = LoggerFactory.getLogger(IntentListCompanies.class);

    
	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		DynamoDB db = new DynamoDB(new AmazonDynamoDBClient());
		Table myStocks = db.getTable(DYNDB_TABLE);

		String userId = session.getUser().getUserId();
		
		HashMap<String, Object> valueMap = new HashMap<>();
		valueMap.put(":userId", userId);
		QuerySpec querySpec = new QuerySpec().withKeyConditionExpression(DYNDB_COL_USER_ID + " = :userId").withValueMap(valueMap);
		
		String companyListStr = "";
        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;
        try {
            items = myStocks.query(querySpec);
            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                String company = item.getString(DYNDB_COL_COMPANY);
                log.info(INTENT_LIST_COMPANIES + " got company: " + company);
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
