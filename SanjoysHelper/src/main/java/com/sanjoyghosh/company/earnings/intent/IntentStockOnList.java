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
				return createStockOnList(em, request, session, company, (int)slotValues.getQuantity().doubleValue());
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


	private SpeechletResponse createStockOnList(EntityManager em, IntentRequest request, Session session, Company company, int quantity) {
		Portfolio portfolio = PortfolioJPA.fetchOrCreatePortfolio(em, session.getUser().getUserId());
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
		outputSpeech.setText("Put " + quantity + " shares of " + company.getName() + " to the list");
		return SpeechletResponse.newTellResponse(outputSpeech);
	}
}
