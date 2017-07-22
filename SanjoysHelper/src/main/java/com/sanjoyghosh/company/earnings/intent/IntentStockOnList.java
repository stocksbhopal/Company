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
import com.sanjoyghosh.company.db.PortfolioJPA;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.Portfolio;
import com.sanjoyghosh.company.db.model.PortfolioItem;

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
	public SpeechletResponse onIntent(IntentRequest request, Session session, IntentResult intentResult) throws SpeechletException {
    	String intentName = request.getIntent().getName();
    	boolean isConfirmation = intentName.equals("AMAZON.YesIntent") || intentName.equals("AMAZON.NoIntent");
    	intentName = isConfirmation ? (String)session.getAttribute(InterfaceIntent.ATTR_LAST_INTENT) : intentName;
    	boolean needsQuantity = 
    		intentName.equals(InterfaceIntent.INTENT_CREATE_STOCK_ON_LIST) ||
    		intentName.equals(InterfaceIntent.INTENT_UPDATE_STOCK_ON_LIST);
    	boolean needsCompany = 
    		!intentName.equals(InterfaceIntent.INTENT_LIST_STOCKS_ON_LIST) && 
    		!intentName.equals(InterfaceIntent.INTENT_CLEAR_STOCKS_ON_LIST);
    	String alexaUserId = session.getUser().getUserId();

    	AllSlotValues slotValues = new AllSlotValues();
    	boolean hasCompanyOrSymbol = IntentUtils.getCompanyOrSymbol(request, session, slotValues);
    	boolean hasQuantity = IntentUtils.getQuantity(request, session, slotValues);
    	if (!hasCompanyOrSymbol && needsCompany) {
    		return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_ERROR_MISSING_COMPANY, slotValues, "Sorry, we need a company name or ticker symbol.");
    	}
    	if (!hasQuantity && needsQuantity) {
    		return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_ERROR_MISSING_QUANTITY, slotValues, "Sorry, we need to know the number of shares.");	
    	}
    	
		EntityManager em = null;
		try {
			em = JPAHelper.getEntityManager();
	    	Company company = CompanyJPA.fetchCompanyByNameOrSymbol(em, slotValues);
	    	if (company == null && needsCompany) {
	    		return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_ERROR_NO_COMPANY_FOUND, slotValues, "Sorry, we found no company identified by " + slotValues.getCompanyOrSymbol());	    		
	    	}
	    	
			if (intentName.equals(InterfaceIntent.INTENT_CREATE_STOCK_ON_LIST)) {
				return createStockOnList(em, alexaUserId, intentName, company, (int)slotValues.getQuantity().doubleValue(), slotValues);
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


	private SpeechletResponse deleteStockOnList(EntityManager em, boolean isConfirmation, String intentName, Session session, Company company, AllSlotValues slotValues) {
		String alexaUserId = session.getUser().getUserId();
		Portfolio portfolio = PortfolioJPA.fetchPortfolio(em, PortfolioJPA.MY_PORTFOLIO_NAME, alexaUserId);
		if (portfolio == null) {
			return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_SUCCESS, null, "Sorry, you do not yet have a list of stocks.");
		}
		PortfolioItem portfolioItem = portfolio.getPortfolioItemBySymbol(company.getSymbol());
		if (portfolioItem == null) {
			return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_SUCCESS, slotValues, "You have no shares of " + company.getName() + " on your list.");
		}

		if (isConfirmation) {
			boolean isYes = intentName.equals("AMAZON.YesIntent");
			if (isYes) {
				try {
					em.getTransaction().begin();					
					em.remove(portfolioItem);
					em.getTransaction().commit();
					return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_SUCCESS, slotValues, "Removed the shares of " + company.getName() + " from the list.");				
				}
				catch (Exception e) {
					logger.log(Level.SEVERE, "Exception in deleting PortfolioItem", e);
					if (em.getTransaction().isActive()) {
						em.getTransaction().rollback();
					}
					return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_ERROR_EXCEPTION, slotValues, "Sorry, there was a problem deleting shares of " + company.getName() + " on your list.", e);
				}
			}
			else {
				return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_SUCCESS, slotValues, "Ignoring the request to delete the shares of " + company.getName() + " from the list.");				
			}
		}
		else {
			String text = "Please confirm that you want to remove the shares of " + company.getName() + " from your list.";
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText(text);
			
			Reprompt reprompt = new Reprompt();
			PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
			repromptSpeech.setText("Sorry, " + text);
			reprompt.setOutputSpeech(repromptSpeech);
			
			session.setAttribute(InterfaceIntent.ATTR_LAST_INTENT, intentName);
			session.setAttribute(InterfaceIntent.ATTR_SYMBOL, company.getSymbol());
			return SpeechletResponse.newAskResponse(outputSpeech, reprompt);			
		}
	}


	private SpeechletResponse clearStocksOnList(EntityManager em, boolean isConfirmation, String intentName, Session session) {
		String alexaUserId = session.getUser().getUserId();
		Portfolio portfolio = PortfolioJPA.fetchPortfolio(em, PortfolioJPA.MY_PORTFOLIO_NAME, session.getUser().getUserId());
		if (portfolio == null) {
			return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_SUCCESS, null, "Sorry, you do not yet have a list of stocks to clear.");
		}
		if (portfolio.isEmpty()) {
			return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_SUCCESS, null, "Sorry, you have no stocks on your list to clear,");
		}
		if (isConfirmation) {
			boolean isYes = intentName.equals("AMAZON.YesIntent");
			if (isYes) {
				try {
					em.getTransaction().begin();		
					for (PortfolioItem portfolioItem : portfolio.getPortfolioItemList()) {
						em.remove(portfolioItem);
					}
					em.remove(portfolio);
					em.getTransaction().commit();
					return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_SUCCESS, null, "Clearing all stocks from your list.");
				}
				catch (Exception e) {
					logger.log(Level.SEVERE, "Exception in clearing all stocks from the Portfolio", e);
					if (em.getTransaction().isActive()) {
						em.getTransaction().rollback();
					}
					return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_ERROR_EXCEPTION, null, "Sorry, there was a problem clearing all stocks from your list.", e);
				}
			}
			else {
				return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_SUCCESS, null, "Ignoring the request to clear all stocks from your list.");
			}
		}
		else {
			String speechText = "Please confirm that you want to clear all stocks on your list.";
			session.setAttribute(InterfaceIntent.ATTR_LAST_INTENT, intentName);
			return IntentUtils.makeAskResponse(alexaUserId, intentName, RESULT_SUCCESS, null, speechText);						
		}
	}


	private SpeechletResponse updateStockOnList(EntityManager em, boolean isConfirmation, String intentName, Session session, Company company, int quantity, AllSlotValues slotValues) {
		String alexaUserId = session.getUser().getUserId();
		Portfolio portfolio = PortfolioJPA.fetchPortfolio(em, PortfolioJPA.MY_PORTFOLIO_NAME, alexaUserId);
		if (portfolio == null) {
			return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_SUCCESS, null, "Sorry, you do not yet have a list of stocks.");
		}
		PortfolioItem portfolioItem = portfolio.getPortfolioItemBySymbol(company.getSymbol());
		if (portfolioItem == null) {
			return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_SUCCESS, slotValues, "You have no shares of " + company.getName() + " on your list.");
		}

		if (isConfirmation) {
			boolean isYes = intentName.equals("AMAZON.YesIntent");
			if (isYes) {
				try {
					em.getTransaction().begin();					
					portfolioItem.setQuantity(quantity);
					em.persist(portfolio);
					em.getTransaction().commit();
					return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_SUCCESS, slotValues, "Changed the number of shares of " + company.getName() + " to " + quantity + ".");				
				}
				catch (Exception e) {
					logger.log(Level.SEVERE, "Exception in updating the quantity in PortfolioItem", e);
					if (em.getTransaction().isActive()) {
						em.getTransaction().rollback();
					}
					return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_ERROR_EXCEPTION, slotValues, "Sorry, could not update the number of shares of " + company.getName() + " on your list.", e);
				}
			}
			else {
				return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_SUCCESS, slotValues, "Ignoring the request to change the number of shares.");				
			}
		}
		else {
			String text = "Please confirm that you want " + quantity + " shares of " + company.getName() + " on your list.";
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText(text);
			
			Reprompt reprompt = new Reprompt();
			PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
			repromptSpeech.setText("Sorry, " + text);
			reprompt.setOutputSpeech(repromptSpeech);
			
			session.setAttribute(InterfaceIntent.ATTR_LAST_INTENT, intentName);
			session.setAttribute(InterfaceIntent.ATTR_QUANTITY, new Double(quantity));
			session.setAttribute(InterfaceIntent.ATTR_SYMBOL, company.getSymbol());
			return SpeechletResponse.newAskResponse(outputSpeech, reprompt);			
		}
	}


	private SpeechletResponse readStockOnList(EntityManager em, String alexaUserId, String intentName, Company company, AllSlotValues slotValues) {
		Portfolio portfolio = PortfolioJPA.fetchPortfolio(em, PortfolioJPA.MY_PORTFOLIO_NAME, alexaUserId);
		if (portfolio == null) {
			return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_SUCCESS, null, "Sorry, you do not yet have a list of stocks.");
		}
		PortfolioItem portfolioItem = portfolio.getPortfolioItemBySymbol(company.getSymbol());
		String speechText = (portfolioItem == null) ?
			"You have no shares of " + company.getName() + " on your list." :
			"You have " + (int)portfolioItem.getQuantity() + " shares of " + company.getName() + " on your list.";
		return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_SUCCESS, company.getSymbol(), slotValues, speechText);
	}


	private SpeechletResponse createStockOnList(EntityManager em, String alexaUserId, String intentName, Company company, int quantity, AllSlotValues slotValues) {
		Portfolio portfolio = PortfolioJPA.fetchOrCreatePortfolio(em, alexaUserId);
		PortfolioItem portfolioItem = portfolio.getPortfolioItemBySymbol(company.getSymbol());
		if (portfolioItem != null) {
			String speechText = "Sorry, you already have " + (int) portfolioItem.getQuantity() + " shares of " + company.getName() + " on your list.";
			return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_SUCCESS, company.getSymbol(), slotValues, speechText);
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
			return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_SUCCESS, company.getSymbol(), slotValues, speechText);
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Exception in adding PortfolioItem to Portfolio", e);
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			
			String speechText = "Sorry, could not add " + quantity + " shares of " + company.getName() + " to your list.";
			return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_ERROR_EXCEPTION, company.getSymbol(), slotValues, speechText, e);
		}	
	}


	private SpeechletResponse listStocksOnList(EntityManager em, String alexaUserId, String intentName) {
		int numStocks = 0;
		String speechText = "";
		Portfolio portfolio = PortfolioJPA.fetchPortfolio(em, PortfolioJPA.MY_PORTFOLIO_NAME, alexaUserId);
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
		
		return IntentUtils.makeTellResponse(alexaUserId, intentName, RESULT_SUCCESS, String.valueOf(numStocks), null, speechText);
	}
}
