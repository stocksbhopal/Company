package com.sanjoyghosh.company.earnings.intent;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.PortfolioItemData;
import com.sanjoyghosh.company.db.PortfolioJPA;
import com.sanjoyghosh.company.utils.LocalDateRange;

public class IntentGetStockEarnings implements InterfaceIntent {

    private static final Logger logger = Logger.getLogger(IntentGetStockEarnings.class.getName());

	public static final int RESULT_SUCCESS = 0;
	public static final int RESULT_ERROR_INVALID_DATE_RANGE = -1;
    
    
    private SpeechletResponse respondWithEarningsInfo(EntityManager em, IntentRequest request, Session session, LocalDateRange dateRange, IntentResult intentResult) {
		String speech = "";
		List<PortfolioItemData> portfolioItemDataList = PortfolioJPA.fetchPortfolioItemDataWithEarnings(
			em, PortfolioJPA.MY_PORTFOLIO_NAME, session.getUser().getUserId(), dateRange.getStartDate(), dateRange.getEndDate());
		if (portfolioItemDataList == null || portfolioItemDataList.size() == 0) {
			speech = "Sorry you don't have any stocks with earnings " + dateRange.toAlexaString() + ".";
		}
		else {
			int length = portfolioItemDataList.size();
			speech = "You have " + length + (length == 1 ? " stock " : " stocks ") + "with earnings " + dateRange.toAlexaString() + ". ";
			int valueChange = (int)PortfolioUtils.getNetValueChange(portfolioItemDataList, intentResult);
			speech += "For a net " + (valueChange >= 0 ? "gain" : "loss") + " of " + valueChange + " dollars. ";
			
			Collections.sort(portfolioItemDataList, new Comparator<PortfolioItemData>() {
				@Override
				public int compare(PortfolioItemData o1, PortfolioItemData o2) {
					return -(new Double(o1.getValueChangeDollars()).compareTo(o2.getValueChangeDollars()));
				}
			});
			for (PortfolioItemData portfolioItemData : portfolioItemDataList) {
				speech += (int)portfolioItemData.getQuantity() + " shares of " + portfolioItemData.getSpeechName() + ", ";
				if (portfolioItemData.getValueChangeDollars() >= 0.00) {
					speech += "gain " + (int)portfolioItemData.getValueChangeDollars() + " dollars, up " + portfolioItemData.getPriceChangePercent() + " percent. ";
				}
				else {
					speech += "loss " + (int)(-portfolioItemData.getValueChangeDollars()) + " dollars, down " + -portfolioItemData.getPriceChangePercent() + " percent. ";					
				}
			}
		}
		logger.info(speech);
		
		intentResult.setResult(RESULT_SUCCESS);
		intentResult.setResponse(String.valueOf(portfolioItemDataList == null ? 0 : portfolioItemDataList.size()));
		
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(speech);
		return SpeechletResponse.newTellResponse(outputSpeech);
    }
    
    
    private SpeechletResponse respondToInvalidTimeFrame(IntentRequest request, Session session, IntentResult intentResult) {
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("Need a time frame less than 31 days ahead, like today or next week.");

		Reprompt reprompt = new Reprompt();
		PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
		repromptSpeech.setText("Sorry, need a time frame less than 31 days ahead, like today or next week.");
		reprompt.setOutputSpeech(repromptSpeech);
		
		logger.info(request.getIntent().getName() + " user did not provide a proper time frame.");
		intentResult.setResult(RESULT_ERROR_INVALID_DATE_RANGE);
		return SpeechletResponse.newAskResponse(outputSpeech, reprompt);	    				    	
    }
    
    
	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session, IntentResult result) throws SpeechletException {
		SpeechletResponse response = null;
		EntityManager em = null;
		try {
			em = JPAHelper.getEntityManager();
			LocalDateRange dateRange = IntentUtils.getValidDateRange(result);
			if (dateRange == null) {
				response = respondToInvalidTimeFrame(request, session, result);
			}
			else {
				response = respondWithEarningsInfo(em, request, session, dateRange, result);
			}
			return response;
		}
		finally {
			if (em != null) {
				em.close();
			}
		}
	}
}
