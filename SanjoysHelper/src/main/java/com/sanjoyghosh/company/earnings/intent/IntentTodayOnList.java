package com.sanjoyghosh.company.earnings.intent;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.PortfolioItemData;
import com.sanjoyghosh.company.db.PortfolioJPA;
import com.sanjoyghosh.company.db.model.Portfolio;

public class IntentTodayOnList implements InterfaceIntent {

    public static final int RESULT_SUCCESS = 0;
    public static final int RESULT_ERROR_EXCEPTION = -1;
    public static final int RESULT_ERROR_MISSING_QUANTITY = -2;
    public static final int RESULT_ERROR_BAD_INTENT = -3;
    
    private static final int DEFAULT_NUM_RESULTS = 6;
    
    @Override
	public SpeechletResponse onIntent(IntentRequest request, Session session, IntentResult intentResult) throws SpeechletException {
    	String intentName = request.getIntent().getName();
    	String alexaUserId = session.getUser().getUserId();

    	AllSlotValues slotValues = new AllSlotValues();
    	boolean hasQuantity = IntentUtils.getQuantity(request, session, slotValues);
    	int numResults = (int) (hasQuantity ? slotValues.getQuantity() : DEFAULT_NUM_RESULTS);
    	
		EntityManager em = null;
		try {
			em = JPAHelper.getEntityManager();	    	
			if (intentName.equals(InterfaceIntent.INTENT_TODAY_PERFORMANCE)) {
				return processTodayPerformance(em, alexaUserId, intentName, intentResult, -1, false, false);
			}
			if (intentName.equals(InterfaceIntent.INTENT_TODAY_TOP_GAINERS_DOLLARS)) {
				return processTodayPerformance(em, alexaUserId, intentName, intentResult, numResults, true, true);
			}
			if (intentName.equals(InterfaceIntent.INTENT_TODAY_TOP_GAINERS_PERCENTAGE)) {
				return processTodayPerformance(em, alexaUserId, intentName, intentResult, numResults, false, true);
			}
			if (intentName.equals(InterfaceIntent.INTENT_TODAY_TOP_LOSERS_DOLLARS)) {
				return processTodayPerformance(em, alexaUserId, intentName, intentResult, numResults, true, false);
			}
			if (intentName.equals(InterfaceIntent.INTENT_TODAY_TOP_LOSERS_PERCENTAGE)) {
				return processTodayPerformance(em, alexaUserId, intentName, intentResult, numResults, false, false);
			}
		}
		finally {
			if (em != null) {
				em.close();
			}
		}
		
		return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_ERROR_BAD_INTENT, slotValues, "Sorry " + getClass().getName() + " does not know what to do with intent: " + request.getIntent().getName());
	}


    /**
     * 
     * @param em
     * @param alexaUserId
     * @param intentName
     * @param intentResult
     * @param numResults -1 is for TodayPerformance. 1+ for everything else.
     * @param sortByValueChange true for sorting by value change.  false for sorting by percent change.
     * @param showGainers true for showing gainers.  false for showing losers.
     * @return
     */
	private SpeechletResponse processTodayPerformance(EntityManager em, String alexaUserId, String intentName, IntentResult intentResult,
		int numResults, boolean sortByValueChange, boolean showGainers) {
		
		String speechText = "";
		Portfolio portfolio = PortfolioJPA.fetchPortfolio(em, PortfolioJPA.MY_PORTFOLIO_NAME, alexaUserId);
		if (portfolio == null || portfolio.isEmpty()) {
			speechText = "Sorry, you do not yet have a list of stocks.";
		}
		else {
			int numGainers = 0;
			int numLosers = 0;
			double netChange = 0.00D;
			
			List<PortfolioItemData> portfolioItemDataList = PortfolioUtils.getPortfolioValueChange(portfolio, intentResult);
			// If numResults is not -1, the results have to be sorted.
			if (numResults > 0) {
				PortfolioUtils.sortPortfolioItemDataList(portfolioItemDataList, sortByValueChange);
				if (showGainers) {
					Collections.reverse(portfolioItemDataList);
				}
			}
			
			for (PortfolioItemData item : portfolioItemDataList) {
				netChange += item.getValueChangeDollars();
				if (item.getPriceChange() >= 0.00D) {
					numGainers++;
				}
				else {
					numLosers++;
				}
			}
			
			speechText = "You have a net " + (netChange >= 0.00D ? "gain of " + netChange : "loss of " + -netChange) + " today. ";
			speechText += "There are " + numGainers + " gainers, and " + numLosers + " losers. ";
			
			if (numResults > 0) {
				speechText = "The top " + numResults + (showGainers ? " gainers" : "losers") + " by " + 
					(sortByValueChange ? "value change" : "percent change") + " are: ";
				for (PortfolioItemData portfolioItemData : portfolioItemDataList) {
					speechText += (int)portfolioItemData.getQuantity() + " shares of " + portfolioItemData.getSpeechName() + ", ";
					if (portfolioItemData.getValueChangeDollars() >= 0.00) {
						speechText += "gain " + (int)portfolioItemData.getValueChangeDollars() + " dollars, up " + portfolioItemData.getPriceChangePercent() + " percent. ";
					}
					else {
						speechText += "loss " + (int)(-portfolioItemData.getValueChangeDollars()) + " dollars, down " + -portfolioItemData.getPriceChangePercent() + " percent. ";					
					}
				}
			}
		}		
		
		return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_SUCCESS, String.valueOf(0), null, speechText);
	}
}
