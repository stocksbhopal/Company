package com.sanjoyghosh.company.earnings.intent;

import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.sanjoyghosh.company.api.CompanyPrice;
import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;

public class IntentMyStocksMovers implements InterfaceIntent {

    private static final Logger log = LoggerFactory.getLogger(IntentMyStocksMovers.class);

	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		String intentName = request.getIntent().getName();
		boolean gainers = intentName.equals(INTENT_MY_STOCKS_GAINERS);
		
		Slot numberSlot = request.getIntent().getSlot("number");
		int number = 0;
		try {
			number = Integer.parseInt(numberSlot.getValue());
		}
		catch (Exception e) {
			log.error("Unparseable int: " + numberSlot.getValue(), e);
			
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText("Sanjoy needs an integer and not " + numberSlot.getValue());
			return SpeechletResponse.newTellResponse(outputSpeech);
		}
		
        EntityManager entityManager = JPAHelper.getEntityManager();
        List<CompanyPrice> companyPriceList = CompanyUtils.fetchCompanyPriceListForAlexaUserLimit(entityManager, session.getUser().getUserId(), number, gainers);
		
        String speech = "Your " + (gainers ? ("top " + number + " gainers") : ("bottom " + number + " losers")) + " are ";
        for (CompanyPrice companyPrice : companyPriceList) {
        	speech += companyPrice.getName() + " at " + companyPrice.getPrice() + 
        		(companyPrice.getPriceChange() > 0.00D ? " up " : " down ") + companyPrice.getPriceChangePercent() + " percent, ";
        }
        
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(speech);
		return SpeechletResponse.newTellResponse(outputSpeech);
	}
}
