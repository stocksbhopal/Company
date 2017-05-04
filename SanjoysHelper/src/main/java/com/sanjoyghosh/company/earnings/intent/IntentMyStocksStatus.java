package com.sanjoyghosh.company.earnings.intent;

import java.util.List;

import javax.persistence.EntityManager;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.sanjoyghosh.company.api.CompanyPrice;
import com.sanjoyghosh.company.db.JPAHelper;

public class IntentMyStocksStatus implements InterfaceIntent {

	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session, IntentResult intentResult) throws SpeechletException {
        EntityManager entityManager = JPAHelper.getEntityManager();
//        List<CompanyPrice> companyPriceList = CompanyUtils.fetchCompanyPriceListForAlexaUser(entityManager, session.getUser().getUserId());
        List<CompanyPrice> companyPriceList = null;
        String speech = "You have no stocks.";
        String intentName = request.getIntent().getName();
        if (intentName.equals(INTENT_MY_STOCKS_STATUS)) {
        	int upCount = 0;
        	int downCount = 0;
        	for (CompanyPrice companyPrice : companyPriceList) {
        		if (companyPrice.getPriceChangePercent() > 0.00D) {
        			upCount++;
        		}
        		else {
        			downCount++;
        		}
        	}
        	
        	if (companyPriceList.size() > 0) {
	        	CompanyPrice topGainer = companyPriceList.get(0);
	        	CompanyPrice bottomGainer = companyPriceList.get(companyPriceList.size() - 1);
	        	speech = upCount + " stocks are up today. " + downCount + " are down. ";
	        	speech += "The top gainer is " + topGainer.getName() + " at " + topGainer.getPriceChangePercent() + " percent. ";
	        	speech += "The bottom gainer is " + bottomGainer.getName() + " at " + bottomGainer.getPriceChangePercent() + " percent.";
        	}
        }
        
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(speech);
		return SpeechletResponse.newTellResponse(outputSpeech);
	}
}
