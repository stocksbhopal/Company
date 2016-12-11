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
    
    
    private StartDateEnum getStartDateSlot(Intent intent) {
		Slot startDateSlot = intent.getSlot(SLOT_START_DATE);
		if (startDateSlot == null) {
			return null;
		}
		
		String startDateStr = startDateSlot.getValue();
		log.info("StartDate: " + startDateStr);
		StartDateEnum startDateEnum = StartDateEnum.valueOf(startDateStr);
		return startDateEnum;
    }

    /*
     * (non-Javadoc)
     * @see com.sanjoyghosh.company.earnings.intent.InterfaceIntent#onIntent(com.amazon.speech.speechlet.IntentRequest, com.amazon.speech.speechlet.Session)
{
  "intents": [
    {"intent": "AMAZON.YesIntent"},
    
    {"intent": "GetStockPrice","slots": [{"name": "company","type": "COMPANY_NAMES"}]},
    
    {"intent": "AddCompany","slots": [{"name": "company","type": "COMPANY_NAMES"}]},
    
    {"intent": "ListCompanies"},
    
    {"intent": "ListEarningsBy","slots": [{"name": "date","type": "DATE"}]},
    {"intent": "ListIndexEarningsBy","slots": [{"name": "index","type": "MARKET_INDEX"},{"name": "date","type": "DATE"}]},
    
    {"intent": "ListEarningsOn","slots": [{"name": "date","type": "DATE"}]},
    {"intent": "ListIndexEarningsOn","slots": [{"name": "index","type": "MARKET_INDEX"},{"name": "date","type": "DATE"}]},
    
    {"intent": "ListEarningsNext"},
    {"intent": "ListIndexEarningsNext","slots": [{"name": "index","type": "MARKET_INDEX"}]},
    
    {"intent": "ListEarningsMyNext"},
    {"intent": "ListIndexEarningsMyNext","slots": [{"name": "index","type": "MARKET_INDEX"}]}
  ]
}

ListEarningsBy for earnings by {date}
ListIndexEarningsBy for {index} earnings by {date}

ListEarningsOn for earnings on {date}
ListIndexEarningsOn for {index} earnings on {date}

ListEarningsNext for next earnings
ListIndexEarningsNext for next {index} earnings

ListEarningsMyNext for my next earnings
ListIndexEarningsMyNext for my next {index} earnings

AddCompany to add {company}

ListCompanies to list my companies

GetStockPrice price of {company}
GetStockPrice the price of {company}
     */

    
    private List<CompanyEarnings> getIndexEarningsNext() {
    	LocalDate startDate = LocalDate.now();
		EntityManager entityManager = JPAHelper.getEntityManager();
		List<CompanyEarnings> earningsList = CompanyUtils.fetchEarningsDateListForMarketIndexNext(entityManager, 
			startDate, MarketIndexEnum.SnP500);
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
			earningsList = getIndexEarningsNext();
		}
		else {
			startDateEnum = getStartDateSlot(intent);
			endDate = getDateSlot(intent);
			LocalDate startDate = startDateEnum == StartDateEnum.on ? endDate : LocalDate.now();
			
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
		outputSpeech.setText("Found " + earningsList.size() + " earnings in My Stocks " + 
			startDateEnum.toString() + DateUtils.toDateString(endDate) +", " + companys);
		return SpeechletResponse.newTellResponse(outputSpeech);		    	
	}
}
