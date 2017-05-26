package com.sanjoyghosh.company.earnings.intent;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.sanjoyghosh.company.db.CompanyJPA;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.PortfolioItemData;
import com.sanjoyghosh.company.db.PortfolioJPA;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.Portfolio;
import com.sanjoyghosh.company.db.model.PortfolioItem;

public class IntentTodayOnList implements InterfaceIntent {

    private static final Logger logger = Logger.getLogger(IntentTodayOnList.class.getName());
   
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
    	int numTopStocks = (int) (hasQuantity ? slotValues.getQuantity() : DEFAULT_NUM_RESULTS);
    	
		EntityManager em = null;
		try {
			em = JPAHelper.getEntityManager();	    	
			if (intentName.equals(InterfaceIntent.INTENT_TODAY_PERFORMANCE)) {
				return processTodayPerformance(em, alexaUserId, intentName, intentResult);
			}
			if (intentName.equals(InterfaceIntent.INTENT_READ_STOCK_ON_LIST)) {
				return readStockOnList(em, alexaUserId, intentName, company, slotValues);
			}
			// intentName might have been changed for AMAZON.YesIntent and AMAZON.NoIntent.  So get it from the request.
			if (intentName.equals(InterfaceIntent.INTENT_UPDATE_STOCK_ON_LIST)) {
				return updateStockOnList(em, isConfirmation, request.getIntent().getName(), session, company, (int)slotValues.getQuantity().doubleValue(), slotValues);
			}
			if (intentName.equals(InterfaceIntent.INTENT_DELETE_STOCK_ON_LIST)) {
				return deleteStockOnList(em, isConfirmation, request.getIntent().getName(), session, company, slotValues);
			}
			if (intentName.equals(InterfaceIntent.INTENT_LIST_STOCKS_ON_LIST)) {
				return listStocksOnList(em, session.getUser().getUserId(), intentName);
			}
			// intentName might have been changed for AMAZON.YesIntent and AMAZON.NoIntent.  So get it from the request.
			if (intentName.equals(InterfaceIntent.INTENT_CLEAR_STOCKS_ON_LIST)) {
				return clearStocksOnList(em, isConfirmation, request.getIntent().getName(), session);
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
			if ()
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
		}		
		
		return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_SUCCESS, String.valueOf(numStocks), null, speechText);
	}
}
