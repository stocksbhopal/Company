package com.sanjoyghosh.company.earnings.intent;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.source.reuters.ReutersCompanyNewsItem;
import com.sanjoyghosh.company.source.reuters.ReutersCompanyNewsReader;
import com.sanjoyghosh.company.utils.CompanyUtils;
import com.sanjoyghosh.company.utils.LocalDateRange;
import com.sanjoyghosh.company.utils.StringUtils;

public class IntentGetCompanyNews implements InterfaceIntent {

    private static final Logger logger = Logger.getLogger(IntentGetCompanyNews.class.getName());

	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session, IntentResult intentResult) throws SpeechletException {
		String intentName = request.getIntent().getName();
		AllSlotValues slotValues = new AllSlotValues();
		if (!IntentUtils.getCompanyOrSymbol(request, session, slotValues)) {
			if (intentName.equals(InterfaceIntent.INTENT_GET_COMPANY_HEADLINES)) {
				return IntentUtils.respondWithQuestion(intentResult, request, session, "Headlines for ", " the headlines.");
			}
			else {
				return IntentUtils.respondWithQuestion(intentResult, request, session, "News for ", " the news.");				
			}
		}
		else {
			logger.info("Company ASR: " + slotValues);
			Company company = CompanyUtils.getCompany(slotValues);
			if (company == null) {
				return IntentUtils.makeTellResponse(session.getUser().getUserId(), intentName, 0, slotValues,
					"Sorry, could not find a company named " + slotValues.toString());
			}
			
			LocalDateRange dateRange = IntentUtils.getDateRange(request);
			LocalDate localDate = dateRange == null ? LocalDate.now() : dateRange.getStartDate();
			List<ReutersCompanyNewsItem> newsItems = null;
			try {
				newsItems = ReutersCompanyNewsReader.readReutersCompanyNews(company, localDate);
			} 
			catch (IOException e) {
				return IntentUtils.makeTellResponse(session.getUser().getUserId(), intentName, 0, slotValues, 
					"Exception in reading News for " + company.getName(), e);
			}
			intentResult.setResult(newsItems.size());
			
			boolean isSsml = false;
			String speechText = "";
			if (intentName.equals(InterfaceIntent.INTENT_GET_COMPANY_HEADLINES)) {
				if (newsItems.size() == 0) {
					speechText = "Sorry, there are no headlines for " + company.getName() + " for " + localDate;
				}
				else {
					List<String> headlineList = new ArrayList<>();
					for (ReutersCompanyNewsItem item : newsItems) {
						headlineList.add("<p>" + item.getHeadline() + "</p>");
					}
					
					isSsml = true;
					speechText = StringUtils.formatForSSML(headlineList);
				}
			}
			else {
				if (newsItems.size() == 0) {
					speechText = "Sorry, there are no news items for " + company.getName() + " for " + localDate;
				}
				else {
					List<String> summaryList = new ArrayList<>();
					for (ReutersCompanyNewsItem item : newsItems) {
						summaryList.add("<p>" + item.getSummary() + "</p>");
					}
					isSsml = true;
					speechText = StringUtils.formatForSSML(summaryList);
				}
			}
			return IntentUtils.makeTellResponse(session.getUser().getUserId(), intentName, newsItems.size(), slotValues, isSsml, speechText);
		}
	}
}
