package com.sanjoyghosh.company.earnings.intent;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.sanjoyghosh.company.api.CompanyEarnings;
import com.sanjoyghosh.company.api.MarketIndexEnum;
import com.sanjoyghosh.company.api.StartDateEnum;
import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.utils.DateUtils;
import com.sanjoyghosh.company.utils.StringUtils;

public class IntentListEarnings implements InterfaceIntent {

    private static final Logger log = LoggerFactory.getLogger(IntentListEarnings.class);

    
    private LocalDate getDateSlot(Intent intent) {
		Slot dateSlot = intent.getSlot(SLOT_DATE);
		if (dateSlot == null) {
			return null;
		}
		
		String dateStr = dateSlot.getValue();
		log.info("Date: " + dateStr);
		try {
			LocalDate date = DateUtils.getDateFromAlexa(dateStr);
			return date;
		}
		catch (ParseException e) {
			log.error(intent.getName() + " could not parse date: " + dateStr, e);
			return null;
		}
    }
    
    
    private MarketIndexEnum getMarketIndexSlot(Intent intent) {
    	Slot marketIndexSlot = intent.getSlot(SLOT_MARKET_INDEX);
    	if (marketIndexSlot == null) {
    		return null;
    	}
    	
    	String marketIndexStr = marketIndexSlot.getValue();
    	log.info("MarketIndex: " + marketIndexStr);
    	MarketIndexEnum marketIndexEnum = MarketIndexEnum.toMarketIndexEnum(marketIndexStr);
    	return marketIndexEnum;
    }
    
        
    private List<CompanyEarnings> getIndexEarningsNext(Intent intent) {
    	LocalDate startDate = LocalDate.now();
		MarketIndexEnum marketIndex = getMarketIndexSlot(intent);
		EntityManager entityManager = JPAHelper.getEntityManager();
		List<CompanyEarnings> earningsList = CompanyUtils.fetchEarningsDateListForMarketIndexNext(entityManager, 
			startDate, marketIndex);
    	return earningsList;
    }
    
    
    private List<String> getMyStockSymbols(Session session) {
		List<String> symbols = new LinkedList<>();
		String userId = session.getUser().getUserId();
		Iterator<Item> items = DynamoDBUtils.queryMyStocksByUserId(userId);
		while (items.hasNext()) {
			symbols.add(items.next().getString("symbol"));
		}	
		return symbols;
    }
    
    
    private List<CompanyEarnings> getEarningsMyNext(Intent intent, Session session) {
    	LocalDate startDate = LocalDate.now();
		List<String> symbols = getMyStockSymbols(session);
		EntityManager entityManager = JPAHelper.getEntityManager();
		List<CompanyEarnings> earningsList = CompanyUtils.fetchEarningsDateListForNextAndSymbols(entityManager, 
			startDate, symbols);
    	return earningsList;    	
    }
    
    
    private List<CompanyEarnings> getEarningsMyBy(LocalDate endDate, Session session) {
		LocalDate startDate = LocalDate.now();
		List<String> symbols = getMyStockSymbols(session);
		EntityManager entityManager = JPAHelper.getEntityManager();
		List<CompanyEarnings> earningsList = CompanyUtils.fetchEarningsDateListForDateRangeAndSymbols(entityManager, 
			startDate, endDate, symbols);
		return earningsList;
    }
    
    
	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		Intent intent = request.getIntent();
		String intentName = intent.getName();
		
		LocalDate endDate = LocalDate.now();
		StartDateEnum startDateEnum = StartDateEnum.on;
		List<CompanyEarnings> earningsList = null;
		
		if (intentName.equals(INTENT_LIST_INDEX_EARNINGS_NEXT)) {
			startDateEnum = StartDateEnum.on;
			earningsList = getIndexEarningsNext(intent);
			if (earningsList.size() > 0) {
				endDate = earningsList.get(0).getEarningsDate();
			}
		}
		else if (intentName.equals(INTENT_LIST_EARNINGS_MY_NEXT)) {
			startDateEnum = StartDateEnum.on;
			earningsList = getEarningsMyNext(intent, session);
			if (earningsList.size() > 0) {
				endDate = earningsList.get(0).getEarningsDate();
			}			
		}
		else if (intentName.equals(INTENT_LIST_EARNINGS_MY_BY)) {
			startDateEnum = StartDateEnum.by;
			endDate = getDateSlot(intent);
			earningsList = getEarningsMyBy(endDate, session);
		}
		else {
			startDateEnum = StartDateEnum.by;
			endDate = getDateSlot(intent);
			LocalDate startDate = LocalDate.now();
			
			List<String> symbols = new LinkedList<>();
			String userId = session.getUser().getUserId();
			Iterator<Item> items = DynamoDBUtils.queryMyStocksByUserId(userId);
			while (items.hasNext()) {
				symbols.add(items.next().getString("symbol"));
			}
			
			EntityManager entityManager = JPAHelper.getEntityManager();
			earningsList = CompanyUtils.fetchEarningsDateListForDateRangeAndSymbols(entityManager, 
				startDate, endDate, symbols);
		}
		
		String companys = "";
		for (CompanyEarnings ce : earningsList) {
			companys += StringUtils.stripTrailingCompanyTypeFromName(ce.getName()) + ", ";
		}
		
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("There are " + earningsList.size() + " earnings " + 
			startDateEnum.toString() + DateUtils.toDateString(endDate) +", " + companys);
		return SpeechletResponse.newTellResponse(outputSpeech);		    	
	}
}
