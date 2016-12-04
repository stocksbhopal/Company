package com.sanjoyghosh.company.earnings.intent;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.sanjoyghosh.company.utils.DateUtils;

public class IntentListEarnings implements InterfaceIntent {

    private static final Logger log = LoggerFactory.getLogger(IntentListEarnings.class);

    
	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		String intentName = request.getIntent().getName();

		Date endDate = null;
		String dateStr = request.getIntent().getSlot(SLOT_DATE).getValue();
		log.info("Date: " + dateStr);
		try {
			endDate = DateUtils.getDateFromAlexa(dateStr);
		} 
		catch (ParseException e) {
			String error = intentName + " could not parse date: " + dateStr;
			log.error(error, e);
			
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText(error);
			return SpeechletResponse.newTellResponse(outputSpeech);		    	
		}
		
		Timestamp startTimestamp = (intentName.endsWith("On") ? new Timestamp(endDate.getTime()) : new Timestamp(new Date().getTime()));
		Timestamp endTimestamp = new Timestamp(endDate.getTime());
		
		List<String> symbols = new LinkedList<>();
		String userId = session.getUser().getUserId();
		Iterator<Item> items = DynamoDBUtils.queryMyStocksByUserId(userId);
		while (items.hasNext()) {
			symbols.add(items.next().getString("symbol"));
		}
		
		/*
		EntityManager entityManager = JPAHelper.getEntityManager();
		List<CompanyEarnings> earnings = CompanyUtils.fetchAllEarningsDateForDateRangeAndSymbols(entityManager, 
			startTimestamp, endTimestamp, symbols);
		String companys = "";
		for (CompanyEarnings ce : earnings) {
			companys += ce.getName() + ", ";
		}
		*/
		
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("Found " + "0" + " earnings in My Stocks " + 
			(intentName.endsWith("On") ? "on " : "by ") + dateStr +", " + "Company");
		return SpeechletResponse.newTellResponse(outputSpeech);		    	
	}
}
