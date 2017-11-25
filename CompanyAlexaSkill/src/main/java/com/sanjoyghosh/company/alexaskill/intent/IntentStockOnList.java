package com.sanjoyghosh.company.alexaskill.intent;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.sanjoyghosh.company.dynamodb.CompanyDynamoDB;
import com.sanjoyghosh.company.dynamodb.helper.CompanyMatcher;
import com.sanjoyghosh.company.dynamodb.helper.PortfolioMatcher;
import com.sanjoyghosh.company.dynamodb.model.Company;
import com.sanjoyghosh.company.dynamodb.model.Portfolio;

public class IntentStockOnList implements InterfaceIntent {

    private static final Logger logger = Logger.getLogger(IntentStockOnList.class.getName());
   
    public static final int RESULT_SUCCESS = 0;
    public static final int RESULT_ERROR_EXCEPTION = -1;
    public static final int RESULT_ERROR_STOCK_ALREADY_ON_LIST = -2;
    public static final int RESULT_ERROR_MISSING_COMPANY = -3;
    public static final int RESULT_ERROR_MISSING_QUANTITY = -4;
    public static final int RESULT_ERROR_NO_COMPANY_FOUND = -5;
    public static final int RESULT_ERROR_BAD_INTENT = -6;
    
    
    @Override
	public SpeechletResponse onIntent(IntentRequest request, Session session, IntentResult result) throws SpeechletException {
	    	AllSlotValues slotValues = result.getSlotValues();
	    	
	    	String realIntentName = result.isConfirmation() ? result.getLastIntentName() : result.getName();
	    	boolean needsQuantity = 
	    		realIntentName.equals(InterfaceIntent.INTENT_CREATE_STOCK_ON_LIST) ||
	    		realIntentName.equals(InterfaceIntent.INTENT_UPDATE_STOCK_ON_LIST);
	    	boolean needsCompany = 
	    		!realIntentName.equals(InterfaceIntent.INTENT_LIST_STOCKS_ON_LIST) && 
	    		!realIntentName.equals(InterfaceIntent.INTENT_CLEAR_STOCKS_ON_LIST);
	    	
	    	Company company = IntentUtils.getCompany(result);
	    	if (needsCompany && company == null) {
	    		result.setResult(RESULT_ERROR_MISSING_COMPANY);
	    		result.setSpeech(false, "Sorry, no company found named: " + slotValues);
	    		return IntentUtils.makeTellResponse(result);
	    	}
	    	
	    	Double quantity = IntentUtils.getQuantity(result);
	    	if (needsQuantity && quantity == null) {
	    		result.setResult(RESULT_ERROR_MISSING_QUANTITY);
	    		result.setSpeech(false, "Sorry, we need to know the number of shares");
	    		return IntentUtils.makeTellResponse(result);	
	    	}
    	
		if (realIntentName.equals(InterfaceIntent.INTENT_CREATE_STOCK_ON_LIST)) {
			return createStockOnList(result);
		}
		if (realIntentName.equals(InterfaceIntent.INTENT_READ_STOCK_ON_LIST)) {
			return readStockOnList(result);
		}
		// intentName might have been changed for AMAZON.YesIntent and AMAZON.NoIntent.  So get it from the request.
		if (realIntentName.equals(InterfaceIntent.INTENT_UPDATE_STOCK_ON_LIST)) {
			return updateStockOnList(session, result);
		}
		if (realIntentName.equals(InterfaceIntent.INTENT_DELETE_STOCK_ON_LIST)) {
			return deleteStockOnList(session, result);
		}
		if (realIntentName.equals(InterfaceIntent.INTENT_LIST_STOCKS_ON_LIST)) {
			return listStocksOnList(result);
		}
		// intentName might have been changed for AMAZON.YesIntent and AMAZON.NoIntent.  So get it from the request.
		if (realIntentName.equals(InterfaceIntent.INTENT_CLEAR_STOCKS_ON_LIST)) {
			return clearStocksOnList(session, result);
		}
		
		return null;
	}


	private SpeechletResponse deleteStockOnList(Session session, IntentResult result) {
		Company company = IntentUtils.getCompany(result);
		Portfolio portfolioItem = PortfolioUtils.getPortfolioItem(result.getAlexaUserId(), company.getSymbol());
		if (portfolioItem == null) {
			result.setSpeech(false, "You have no shares of " + company.getName() + " on your list.");
			return IntentUtils.makeTellResponse(result);
		}

		if (result.isConfirmation()) {
			if (result.isYesIntent()) {
				try {
					PortfolioUtils.removePortfolioItem(portfolioItem);
					
					result.setSpeech(false, "Removed the shares of " + company.getName() + " from the list.");
					return IntentUtils.makeTellResponse(result);				
				}
				catch (Exception e) {
					logger.log(Level.SEVERE, "Exception in deleting PortfolioItem", e);
					result.setResult(RESULT_ERROR_EXCEPTION);
					result.setThrown(e);
					result.setSpeech(false, "Sorry, there was a problem deleting shares of " + company.getName() + " on your list.");
					return IntentUtils.makeTellResponse(result);
				}
			}
			else {
				result.setSpeech(false, "Ignoring the request to delete the shares of " + company.getName() + " from the list.");
				return IntentUtils.makeTellResponse(result);				
			}
		}
		else {
			String text = "Please confirm that you want to remove the shares of " + company.getName() + " from your list.";
			result.setSpeech(false, text);
			return IntentUtils.makeAskResponse(result, session, text);			
		}
	}


	private SpeechletResponse clearStocksOnList(Session session, IntentResult result) {
		List<Portfolio> portfolioList = PortfolioMatcher.getPortfolioForAlexaUser(result.getAlexaUserId());
		if (portfolioList == null || portfolioList.isEmpty()) {
			result.setSpeech(false, "Sorry, you do not yet have a list of stocks to clear.");
			return IntentUtils.makeTellResponse(result);
		}
		
		if (result.isConfirmation()) {
			if (result.isYesIntent()) {
				try {
					CompanyDynamoDB.batchDeleteDynamoDB((Iterable) portfolioList, "Portfolio");
					
					result.setSpeech(false, "Clearing all stocks from your list.");
					return IntentUtils.makeTellResponse(result);
				}
				catch (Exception e) {
					logger.log(Level.SEVERE, "Exception in clearing all stocks from the Portfolio", e);
					
					result.setResult(RESULT_ERROR_EXCEPTION);
					result.setThrown(e);
					result.setSpeech(false, "Sorry, there was a problem clearing all stocks from your list.");
					return IntentUtils.makeTellResponse(result);
				}
			}
			else {
				result.setSpeech(false, "Ignoring the request to clear all stocks from your list.");
				return IntentUtils.makeTellResponse(result);
			}
		}
		else {
			String promptText = "Please confirm that you want to clear all stocks on your list.";
			result.setSpeech(false, promptText);
			return IntentUtils.makeAskResponse(result, session, promptText);						
		}
	}


	private SpeechletResponse updateStockOnList(Session session, IntentResult result) {
		Company company = IntentUtils.getCompany(result);
		Portfolio portfolioItem = PortfolioUtils.getPortfolioItem(result.getAlexaUserId(), company.getSymbol());
		if (portfolioItem == null) {
			result.setSpeech(false, "You have no shares of " + company.getName() + " on your list.");
			return IntentUtils.makeTellResponse(result);
		}

		int quantity = IntentUtils.getQuantityInt(result);
		if (result.isConfirmation()) {
			if (result.isYesIntent()) {
				try {					
					portfolioItem.setQuantity((double) quantity);
					CompanyDynamoDB.saveObject(portfolioItem);
					
					result.setSpeech(false, "Changed the number of shares of " + company.getName() + " to " + quantity + ".");
					return IntentUtils.makeTellResponse(result);				
				}
				catch (Exception e) {
					result.setResult(RESULT_ERROR_EXCEPTION);
					result.setThrown(e);
					result.setSpeech(false, "Sorry, could not update the number of shares of " + company.getName() + " on your list.");
					return IntentUtils.makeTellResponse(result);
				}
			}
			else {
				result.setSpeech(false, "Ignoring the request to change the number of shares.");
				return IntentUtils.makeTellResponse(result);				
			}
		}
		else {
			String speechText = "Please confirm that you want " + quantity + " shares of " + company.getName() + " on your list.";
			result.setSpeech(false, speechText);
			return IntentUtils.makeAskResponse(result, session, speechText);			
		}
	}


	private SpeechletResponse readStockOnList(IntentResult result) {
		Company company = IntentUtils.getCompany(result);
		Portfolio portfolioItem = PortfolioUtils.getPortfolioItem(result.getAlexaUserId(), company.getSymbol());
		String speechText = (portfolioItem == null) ?
			"You have no shares of " + company.getName() + " on your list." :
			"You have " + portfolioItem.getQuantity().intValue() + " shares of " + company.getName() + " on your list.";
		result.setResponse(company.getSymbol());
		result.setSpeech(false, speechText);
		return IntentUtils.makeTellResponse(result);
	}


	private SpeechletResponse createStockOnList(IntentResult result) {
		// We have already checked for Company in onIntent().
		Company company = IntentUtils.getCompany(result);
		int quantity = IntentUtils.getQuantityInt(result);
		
		Portfolio portfolioItem = PortfolioUtils.getPortfolioItem(result.getAlexaUserId(), company.getSymbol());
		if (portfolioItem != null) {
			String speechText = "Sorry, you already have " + (int) portfolioItem.getQuantity().intValue() + " shares of " + company.getName() + " on your list.";
			result.setSpeech(false, speechText);
			return IntentUtils.makeTellResponse(result);
		}
		
		try {
			portfolioItem = new Portfolio();
			portfolioItem.setAddDate(LocalDate.now());
			portfolioItem.setAlexaUserId(result.getAlexaUserId());
			portfolioItem.setQuantity((double) quantity);
			portfolioItem.setSymbol(company.getSymbol());
			CompanyDynamoDB.saveObject(portfolioItem);
			
			String speechText = "Put " + quantity + " shares of " + company.getName() + " on the list";
			result.setSpeech(false, speechText);
			return IntentUtils.makeTellResponse(result);
		}
		catch (Exception e) {			
			String speechText = "Sorry, could not add " + quantity + " shares of " + company.getName() + " to your list.";
			result.setThrown(e);
			result.setResult(RESULT_ERROR_EXCEPTION);
			result.setSpeech(false, speechText);
			return IntentUtils.makeTellResponse(result);
		}	
	}


	private SpeechletResponse listStocksOnList(IntentResult result) {
		int numStocks = 0;
		String speechText = "";
		
		List<Portfolio> portfolioList = PortfolioMatcher.getPortfolioForAlexaUser(result.getAlexaUserId());
		if (portfolioList == null || portfolioList.isEmpty()) {
			speechText = "Sorry, you do not yet have a list of stocks.";
		}
		else {
			numStocks = portfolioList.size();
			speechText = "You have ";
			for (Portfolio item : portfolioList) {
				Company company = CompanyMatcher.getCompanyBySymbol(item.getSymbol());
				String companyName = company != null ? company.getName() : "NO COMPANY NAME";
				speechText += (int)item.getQuantity().doubleValue() + " shares of " + companyName + ", ";
			}
			speechText += "on your list.";
		}	
		
		result.setResult(numStocks);
		result.setSpeech(false, speechText);
		return IntentUtils.makeTellResponse(result);
	}
}
