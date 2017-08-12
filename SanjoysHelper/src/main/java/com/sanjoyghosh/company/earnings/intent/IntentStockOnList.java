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
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.PortfolioJPA;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.Portfolio;
import com.sanjoyghosh.company.db.model.PortfolioItem;
import com.sanjoyghosh.company.utils.StringUtils;

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
    	
    	Company company = result.getSlotValues().getCompany();
    	if (needsCompany && company == null) {
    		result.setResult(RESULT_ERROR_MISSING_COMPANY);
    		result.setSpeech(false, "Sorry, no company found named: " + slotValues);
    		return IntentUtils.makeTellResponse(result);
    	}
    	
    	if (needsQuantity && result.getSlotValues().getQuantity() == null) {
    		result.setResult(RESULT_ERROR_MISSING_QUANTITY);
    		result.setSpeech(false, "Sorry, we need to know the number of shares");
    		return IntentUtils.makeTellResponse(result);	
    	}
    	
		EntityManager em = null;
		try {	
			em = JPAHelper.getEntityManager();
			
			if (realIntentName.equals(InterfaceIntent.INTENT_CREATE_STOCK_ON_LIST)) {
				return createStockOnList(em, result);
			}
			if (realIntentName.equals(InterfaceIntent.INTENT_READ_STOCK_ON_LIST)) {
				return readStockOnList(em, result);
			}
			// intentName might have been changed for AMAZON.YesIntent and AMAZON.NoIntent.  So get it from the request.
			if (realIntentName.equals(InterfaceIntent.INTENT_UPDATE_STOCK_ON_LIST)) {
				return updateStockOnList(em, session, result);
			}
			if (realIntentName.equals(InterfaceIntent.INTENT_DELETE_STOCK_ON_LIST)) {
				return deleteStockOnList(em, session, result);
			}
			if (realIntentName.equals(InterfaceIntent.INTENT_LIST_STOCKS_ON_LIST)) {
				return listStocksOnList(em, result);
			}
			// intentName might have been changed for AMAZON.YesIntent and AMAZON.NoIntent.  So get it from the request.
			if (realIntentName.equals(InterfaceIntent.INTENT_CLEAR_STOCKS_ON_LIST)) {
				return clearStocksOnList(em, session, result);
			}
		}
		finally {
			if (em != null) {
				em.close();
			}
		}
		
		return null;
	}


	private SpeechletResponse deleteStockOnList(EntityManager em, Session session, IntentResult result) {
		Portfolio portfolio = PortfolioJPA.fetchPortfolio(em, PortfolioJPA.MY_PORTFOLIO_NAME, result.getAlexaUserId());
		if (portfolio == null) {
			result.setSpeech(false, "Sorry, you do not yet have a list of stocks.");
			return IntentUtils.makeTellResponse(result);
		}
		
		Company company = result.getSlotValues().getCompany();
		PortfolioItem portfolioItem = portfolio.getPortfolioItemBySymbol(company.getSymbol());
		if (portfolioItem == null) {
			result.setSpeech(false, "You have no shares of " + company.getName() + " on your list.");
			return IntentUtils.makeTellResponse(result);
		}

		if (result.isConfirmation()) {
			if (result.isYesIntent()) {
				try {
					em.getTransaction().begin();					
					em.remove(portfolioItem);
					em.getTransaction().commit();
					
					result.setSpeech(false, "Removed the shares of " + company.getName() + " from the list.");
					return IntentUtils.makeTellResponse(result);				
				}
				catch (Exception e) {
					logger.log(Level.SEVERE, "Exception in deleting PortfolioItem", e);
					if (em.getTransaction().isActive()) {
						em.getTransaction().rollback();
					}
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


	private SpeechletResponse clearStocksOnList(EntityManager em, Session session, IntentResult result) {
		String alexaUserId = result.getAlexaUserId();
		
		Portfolio portfolio = PortfolioJPA.fetchPortfolio(em, PortfolioJPA.MY_PORTFOLIO_NAME, alexaUserId);
		if (portfolio == null) {
			result.setSpeech(false, "Sorry, you do not yet have a list of stocks to clear.");
			return IntentUtils.makeTellResponse(result);
		}
		
		if (result.isConfirmation()) {
			if (result.isYesIntent()) {
				try {
					em.getTransaction().begin();		
					for (PortfolioItem portfolioItem : portfolio.getPortfolioItemList()) {
						em.remove(portfolioItem);
					}
					em.remove(portfolio);
					em.getTransaction().commit();
					
					result.setSpeech(false, "Clearing all stocks from your list.");
					return IntentUtils.makeTellResponse(result);
				}
				catch (Exception e) {
					logger.log(Level.SEVERE, "Exception in clearing all stocks from the Portfolio", e);
					if (em.getTransaction().isActive()) {
						em.getTransaction().rollback();
					}
					
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


	private SpeechletResponse updateStockOnList(EntityManager em, Session session, IntentResult result) {
		Portfolio portfolio = PortfolioJPA.fetchPortfolio(em, PortfolioJPA.MY_PORTFOLIO_NAME, result.getAlexaUserId());
		if (portfolio == null) {
			result.setSpeech(false, "Sorry, you do not yet have a list of stocks.");
			return IntentUtils.makeTellResponse(result);
		}
		
		Company company = result.getSlotValues().getCompany();
		PortfolioItem portfolioItem = portfolio.getPortfolioItemBySymbol(company.getSymbol());
		if (portfolioItem == null) {
			result.setSpeech(false, "You have no shares of " + company.getName() + " on your list.");
			return IntentUtils.makeTellResponse(result);
		}

		int quantity = StringUtils.roundToInt(result.getSlotValues().getQuantity());
		if (result.isConfirmation()) {
			if (result.isYesIntent()) {
				try {					
					em.getTransaction().begin();					
					portfolioItem.setQuantity(quantity);
					em.persist(portfolio);
					em.getTransaction().commit();
					
					result.setSpeech(false, "Changed the number of shares of " + company.getName() + " to " + quantity + ".");
					return IntentUtils.makeTellResponse(result);				
				}
				catch (Exception e) {
					if (em.getTransaction().isActive()) {
						em.getTransaction().rollback();
					}
					
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


	private SpeechletResponse readStockOnList(EntityManager em, IntentResult result) {
		Portfolio portfolio = PortfolioJPA.fetchPortfolio(em, PortfolioJPA.MY_PORTFOLIO_NAME, result.getAlexaUserId());
		if (portfolio == null) {
			result.setSpeech(false, "Sorry, you do not yet have a list of stocks.");
			return IntentUtils.makeTellResponse(result);
		}
		
		Company company = result.getSlotValues().getCompany();
		PortfolioItem portfolioItem = portfolio.getPortfolioItemBySymbol(company.getSymbol());
		String speechText = (portfolioItem == null) ?
			"You have no shares of " + company.getName() + " on your list." :
			"You have " + (int)portfolioItem.getQuantity() + " shares of " + company.getName() + " on your list.";
		result.setResponse(company.getSymbol());
		result.setSpeech(false, speechText);
		return IntentUtils.makeTellResponse(result);
	}


	private SpeechletResponse createStockOnList(EntityManager em, IntentResult result) {
		Company company = result.getSlotValues().getCompany();
		int quantity = StringUtils.roundToInt(result.getSlotValues().getQuantity());
		
		Portfolio portfolio = PortfolioJPA.fetchOrCreatePortfolio(em, result.getAlexaUserId());
		PortfolioItem portfolioItem = portfolio.getPortfolioItemBySymbol(company.getSymbol());
		if (portfolioItem != null) {
			String speechText = "Sorry, you already have " + (int) portfolioItem.getQuantity() + " shares of " + company.getName() + " on your list.";
			result.setSpeech(false, speechText);
			return IntentUtils.makeTellResponse(result);
		}
		
		try {
			em.getTransaction().begin();
			portfolioItem = new PortfolioItem();
			portfolioItem.setCompany(company);
			portfolioItem.setCreateDate(LocalDate.now());
			portfolioItem.setPortfolio(portfolio);
			portfolioItem.setQuantity(quantity);
			portfolio.addPortfolioItem(portfolioItem);
			em.persist(portfolio);
			em.getTransaction().commit();
			
			String speechText = "Put " + quantity + " shares of " + company.getName() + " on the list";
			result.setSpeech(false, speechText);
			return IntentUtils.makeTellResponse(result);
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Exception in adding PortfolioItem to Portfolio", e);
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			
			String speechText = "Sorry, could not add " + quantity + " shares of " + company.getName() + " to your list.";
			result.setThrown(e);
			result.setResult(RESULT_ERROR_EXCEPTION);
			result.setSpeech(false, speechText);
			return IntentUtils.makeTellResponse(result);
		}	
	}


	private SpeechletResponse listStocksOnList(EntityManager em, IntentResult result) {
		int numStocks = 0;
		String speechText = "";
		
		Portfolio portfolio = PortfolioJPA.fetchPortfolio(em, PortfolioJPA.MY_PORTFOLIO_NAME, result.getAlexaUserId());
		if (portfolio == null || portfolio.isEmpty()) {
			speechText = "Sorry, you do not yet have a list of stocks.";
		}
		else {
			List<PortfolioItem> portfolioItemList = portfolio.getPortfolioItemList();
			numStocks = portfolioItemList.size();
			speechText = "You have ";
			for (PortfolioItem item : portfolioItemList) {
				speechText += (int)item.getQuantity() + " shares of " + item.getCompany().getName() + ", ";
			}
			speechText += "on your list.";
		}	
		
		result.setResult(numStocks);
		result.setSpeech(false, speechText);
		return IntentUtils.makeTellResponse(result);
	}
}
