package com.sanjoyghosh.company.earnings.intent;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;

public class IntentAddCompany implements InterfaceIntent {

	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		DynamoDB db = new DynamoDB(new AmazonDynamoDBClient());
		Table myStocks = db.getTable("MyStocks");
		
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("Yes My Stocks is open " + (myStocks == null ? "NOT" : "YES"));
		return SpeechletResponse.newTellResponse(outputSpeech);		
	}
}
