package com.sanjoyghosh.company.earnings.intent;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.source.reuters.ReutersCompanyNewsItem;
import com.sanjoyghosh.company.source.reuters.ReutersCompanyNewsReader;
import com.sanjoyghosh.company.utils.CompanyUtils;
import com.sanjoyghosh.company.utils.LocalDateRange;

public class IntentGetCompanyNews implements InterfaceIntent {

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
			Company company = CompanyUtils.getCompany(slotValues);
			if (company == null) {
				return IntentUtils.makeTellResponse(session.getUser().getUserId(), intentName, 0, slotValues, 
					"Sorry, could not find a company named " + slotValues.toString());
			}
			
			LocalDateRange dateRange = IntentUtils.getValidDateRange(request);
			LocalDate localDate = dateRange == null ? LocalDate.now() : dateRange.getStartDate();
			List<ReutersCompanyNewsItem> newsItems = null;
			try {
				newsItems = ReutersCompanyNewsReader.readReutersCompanyNews(company, localDate);
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			intentResult.setResult(newsItems.size());
			
			String speechText = "";
			if (intentName.equals(InterfaceIntent.INTENT_GET_COMPANY_HEADLINES)) {
				if (newsItems.size() == 0) {
					speechText = "Sorry, there are no headlines for " + company.getName() + " for " + localDate;
				}
				else {
					for (ReutersCompanyNewsItem item : newsItems) {
						speechText += StringEscapeUtils.escapeXml(item.getHeadline()) + ", ";
					}
				}
			}
			else {
				if (newsItems.size() == 0) {
					speechText = "Sorry, there are no news items for " + company.getName() + " for " + localDate;
				}
				else {
					for (ReutersCompanyNewsItem item : newsItems) {
						speechText += StringEscapeUtils.escapeXml(item.getSummary()) + ", ";
					}
				}
			}
			return IntentUtils.makeTellResponse(session.getUser().getUserId(), intentName, newsItems.size(), slotValues, speechText);
		}
	}
}
