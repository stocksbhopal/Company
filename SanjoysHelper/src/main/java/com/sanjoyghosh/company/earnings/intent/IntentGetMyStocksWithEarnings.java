package com.sanjoyghosh.company.earnings.intent;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.sanjoyghosh.company.db.PortfolioJPA;
import com.sanjoyghosh.company.earnings.utils.CompanyFacts;
import com.sanjoyghosh.company.utils.LoggerUtils;

public class IntentGetMyStocksWithEarnings implements InterfaceIntent {

    private static final Logger logger = Logger.getLogger(IntentGetMyStocksWithEarnings.class.getPackage().getName());

    
	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		String speech = "";
		List<CompanyFacts> companyFactsList = PortfolioJPA.fetchCompanyFactsForPortfolioWithEarnings(
			PortfolioJPA.MY_PORTFOLIO_NAME, session.getUser().getUserId(), LocalDate.now().minusDays(4), LocalDate.now());
		if (companyFactsList == null) {
			speech = "Sorry you don't have a stock list";
		}
		else {
			speech = "You have " + companyFactsList.size() + " stocks in your list. They are: ";
			for (CompanyFacts companyFacts : companyFactsList) {
				speech += companyFacts.getSpeechName() + ", ";
			}
		}
		logger.info(LoggerUtils.makeLogString(session, speech));
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(speech);
		return SpeechletResponse.newTellResponse(outputSpeech);		    	
	}
}
