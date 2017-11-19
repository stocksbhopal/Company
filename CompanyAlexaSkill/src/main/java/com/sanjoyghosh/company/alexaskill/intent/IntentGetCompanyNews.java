package com.sanjoyghosh.company.alexaskill.intent;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.sanjoyghosh.company.alexaskill.source.reuters.ReutersCompanyNewsItem;
import com.sanjoyghosh.company.alexaskill.source.reuters.ReutersCompanyNewsReader;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.utils.LocalDateRange;
import com.sanjoyghosh.company.utils.StringUtils;

public class IntentGetCompanyNews implements InterfaceIntent {

//    private static final Logger logger = Logger.getLogger(IntentGetCompanyNews.class.getName());

	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session, IntentResult result) throws SpeechletException {
		String intentName = result.getName();
		AllSlotValues slotValues = result.getSlotValues();
		Company company = slotValues.getCompany();
		
		if (company == null) {
			result.setSpeech(false, "Sorry, could not find a company named " + slotValues.toString());
			return IntentUtils.makeTellResponse(result);
		}
		
		LocalDateRange dateRange = IntentUtils.getDateRange(result);
		LocalDate localDate = dateRange == null ? LocalDate.now() : dateRange.getStartDate();
		List<ReutersCompanyNewsItem> newsItems = null;
		try {
			newsItems = ReutersCompanyNewsReader.readReutersCompanyNews(company, localDate);
		} 
		catch (IOException e) {
			result.setSpeech(false, "Exception in reading News for " + company.getName());
			result.setThrown(e);
			return IntentUtils.makeTellResponse(result);
		}
		result.setResult(newsItems.size());
		
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
		result.setResult(newsItems.size());
		result.setSpeech(isSsml, speechText);
		return IntentUtils.makeTellResponse(result);
	}
}
