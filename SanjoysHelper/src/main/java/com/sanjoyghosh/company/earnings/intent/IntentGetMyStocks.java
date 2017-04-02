package com.sanjoyghosh.company.earnings.intent;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.PortfolioJPA;
import com.sanjoyghosh.company.db.model.Portfolio;
import com.sanjoyghosh.company.db.model.PortfolioItem;
import com.sanjoyghosh.company.utils.LoggerUtils;

public class IntentGetMyStocks implements InterfaceIntent {

    private static final Logger logger = Logger.getLogger(IntentGetMyStocks.class.getName());

    
	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		String speech = "";
		EntityManager em = null;
		try {
			em = JPAHelper.getEntityManager();
			Portfolio portfolio = PortfolioJPA.fetchPortfolio(em, PortfolioJPA.MY_PORTFOLIO_NAME, session.getUser().getUserId());
			if (portfolio == null) {
				speech = "Sorry you don't have a stock list";
			}
			else {
				List<PortfolioItem> portfolioItemList = portfolio.getPortfolioItemList();
				speech = "You have " + portfolioItemList.size() + " stocks in your list. They are: ";
				for (PortfolioItem portfolioItem : portfolioItemList) {
					speech += ((int)portfolioItem.getQuantity()) + " shares of " + portfolioItem.getCompany().getSpeechName() + ", ";
				}
			}
			logger.info(LoggerUtils.makeLogString(session.getUser().getUserId(), speech));
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText(speech);
			return SpeechletResponse.newTellResponse(outputSpeech);	
		}
		finally {
			if (em != null) {
				em.close();
			}
		}
	}
}
