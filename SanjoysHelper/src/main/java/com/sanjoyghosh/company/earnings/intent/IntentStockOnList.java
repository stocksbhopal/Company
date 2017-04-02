package com.sanjoyghosh.company.earnings.intent;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.sanjoyghosh.company.db.CompanyJPA;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.PortfolioJPA;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.Portfolio;
import com.sanjoyghosh.company.db.model.PortfolioItem;

public class IntentStockOnList implements InterfaceIntent {

    private static final Logger logger = Logger.getLogger(IntentStockOnList.class.getName());
   
    
    @Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
    	String intentName = request.getIntent().getName();
    	boolean needsQuantity = 
    		intentName.equals(InterfaceIntent.INTENT_CREATE_STOCK_ON_LIST) ||
    		intentName.equals(InterfaceIntent.INTENT_UPDATE_STOCK_ON_LIST);
    	String alexaUserId = session.getUser().getUserId();

    	AllSlotValues slotValues = new AllSlotValues();
    	boolean hasCompanyOrSymbol = IntentUtils.getCompanyOrSymbol(request, slotValues);
    	boolean hasQuantity = IntentUtils.getQuantity(request, slotValues);
    	if (!hasCompanyOrSymbol) {
    		
    	}
    	if (!hasQuantity && needsQuantity) {
    		
    	}
    	
		EntityManager em = null;
		try {
			em = JPAHelper.getEntityManager();
	    	Company company = CompanyJPA.fetchCompanyByNameOrSymbol(em, slotValues);
	    	if (company == null) {
	    		
	    	}
	    	
			if (intentName.equals(InterfaceIntent.INTENT_CREATE_STOCK_ON_LIST)) {
				return createStockOnList(em, request, alexaUserId, company, (int)slotValues.getQuantity().doubleValue());
			}
			if (intentName.equals(InterfaceIntent.INTENT_READ_STOCK_ON_LIST)) {
				return readStockOnList(em, request, alexaUserId, company);
			}
			if (intentName.equals(InterfaceIntent.INTENT_LIST_STOCKS_ON_LIST)) {
				return listStocksOnList(em, session.getUser().getUserId());
			}
		}
		finally {
			if (em != null) {
				em.close();
			}
		}
		
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		return SpeechletResponse.newTellResponse(outputSpeech);
	}


	private SpeechletResponse readStockOnList(EntityManager em, IntentRequest request, String alexaUserId, Company company) {
		Portfolio portfolio = PortfolioJPA.fetchOrCreatePortfolio(em, alexaUserId);
		PortfolioItem portfolioItem = portfolio.getPortfolioItemBySymbol(company.getSymbol());
		String text = "";
		if (portfolioItem == null) {
			text = "You have no shares of " + company.getName() + " on your list.";
		}
		else {
			text = "You have " + (int)portfolioItem.getQuantity() + " shares of " + company.getName() + " on your list.";
		}
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(text);
		return SpeechletResponse.newTellResponse(outputSpeech);
	}


	private SpeechletResponse createStockOnList(EntityManager em, IntentRequest request, String alexaUserId, Company company, int quantity) {
		Portfolio portfolio = PortfolioJPA.fetchOrCreatePortfolio(em, alexaUserId);
		PortfolioItem portfolioItem = portfolio.getPortfolioItemBySymbol(company.getSymbol());
		if (portfolioItem != null) {
			
		}
		
		try {
			em.getTransaction().begin();
			
			portfolioItem = new PortfolioItem();
			portfolioItem.setCompany(company);
			portfolioItem.setCreateDate(LocalDate.now());
			portfolioItem.setPortfolio(portfolio);
			portfolioItem.setQuantity(quantity);
			portfolioItem.setValidateDate(LocalDate.now());
			
			portfolio.addPortfolioItem(portfolioItem);
			
			em.persist(portfolio);
			em.getTransaction().commit();
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Exception in adding PortfolioItem to Portfolio", e);
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
		}
				
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("Put " + quantity + " shares of " + company.getName() + " on the list");
		return SpeechletResponse.newTellResponse(outputSpeech);
	}


	private SpeechletResponse listStocksOnList(EntityManager em, String alexaUserId) {
		Portfolio portfolio = PortfolioJPA.fetchOrCreatePortfolio(em, alexaUserId);
		String text = "Sorry, you have no stocks in your list.";
		if (!portfolio.isEmpty()) {
			text = "You have ";
			for (PortfolioItem item : portfolio.getPortfolioItemList()) {
				text += (int)item.getQuantity() + " shares of " + item.getCompany().getName() + ", ";
			}
			text += " in your list.";
		}
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(text);
		return SpeechletResponse.newTellResponse(outputSpeech);
	}
}
