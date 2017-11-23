package com.sanjoyghosh.company.alexaskill.intent;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.sanjoyghosh.company.dynamodb.helper.CompanyNameMatcher;
import com.sanjoyghosh.company.dynamodb.model.Company;
import com.sanjoyghosh.company.utils.AlexaDateUtils;
import com.sanjoyghosh.company.utils.LocalDateRange;

public class IntentUtils {
	
    private static final Logger logger = Logger.getLogger(IntentUtils.class.getName());


    public static void getSlotsFromIntent(IntentRequest request, IntentResult result) {
		for (Entry<String, Slot> entry : request.getIntent().getSlots().entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue().getValue();
			value = value == null ? null : value.trim();
			if (value != null && value.length() > 0) {
				result.getIntentSlotMap().put(key, value);
			}
		}
	}
	

    public static void getSlotsFromSession(Session session, IntentResult result) {
		for (Entry<String, Object> entry : session.getAttributes().entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue().toString();
			value = value == null ? null : value.trim();
			if (value != null && value.length() > 0) {
				result.getIntentSlotMap().put(key, value);
			}
		}
	}

    
	public static LocalDateRange getDateRange(IntentResult result) {
		if (result.getSlotValues().getLocalDateRange() != null) {
			return result.getSlotValues().getLocalDateRange();
		}
		
		String dateRangeStr = result.getIntentSlotMap().get(InterfaceIntent.SLOT_DATE);
		if (dateRangeStr == null) {
			return null;
		}

		LocalDateRange localDateRange = null;
		if (dateRangeStr == null || dateRangeStr.trim().length() == 0) {
			LocalDate localDate = LocalDate.now(ZoneId.of("America/New_York"));
			localDateRange = new LocalDateRange(localDate, localDate, 1);
		}
		else {
			localDateRange = AlexaDateUtils.getLocalDateRange(dateRangeStr);
		}
		result.getSlotValues().setLocalDateRange(localDateRange);
		return localDateRange;
	}

	
	public static LocalDateRange getValidDateRange(IntentResult result) {
		LocalDateRange localDateRange = getDateRange(result);
		// Alexa Date is local US date, whereas EC2 date is UTC date.
		// And so Alexa Date can be up to 1 day behind EC2 date.
		if (localDateRange.getEndDate().isBefore(LocalDate.now().minusDays(31L))) {
			return null;
		}
		if (localDateRange.getEndDate().isAfter(LocalDate.now().plusDays(31L))) {
			return null;
		}
		return localDateRange;
	}
	
	
	public static Company getCompany(IntentResult result) {
	    	if (result.getSlotValues().getCompany() != null) {
	    		return null;
	    	}
	    	
	    	String companyOrSymbol = result.getIntentSlotMap().get(InterfaceIntent.SLOT_COMPANY);
	    	if (companyOrSymbol == null || companyOrSymbol.trim().length() == 0) {
	    		return null;
	    	}
	    	
	    	result.getSlotValues().setCompanyOrSymbol(companyOrSymbol);
	    	result.getSlotValues().setCompanyOrSymbolSpelt(CompanyNameMatcher.getSymbolSpelt(companyOrSymbol));
	    	
	    Company company = CompanyNameMatcher.getCompanyByNameOrSymbol(companyOrSymbol);
	    result.getSlotValues().setCompany(company);
	    
	    return company;
    }
    
    
    public static boolean getQuantity(IntentResult result) {
    	if (result.getSlotValues().getQuantity() != null) {
    		return true;
    	}
    	
    	String quantityStr = result.getIntentSlotMap().get(InterfaceIntent.SLOT_QUANTITY);    	
    	if (quantityStr == null) {
    		return false;
    	}
    	
    	try {
    		double quantity = Double.parseDouble(quantityStr);
    		result.getSlotValues().setQuantity(quantity);
    		return true;
    	}
    	catch (Exception e) {
    		logger.log(Level.SEVERE, result.getName() + " given bad value for quantity: " + quantityStr, e);
    	}
    	return false;
    }
    

    public static SpeechletResponse makeTellResponse(IntentResult result) {
    	if (result.getThrown() == null) {
			logger.log(Level.INFO, result.getName() + ": " + result.getSpeech());
    	}
    	else {
    		logger.log(Level.SEVERE, result.getName() + ": " + result.getSpeech(), result.getThrown());
    	}

    	if (result.isSsml()) {
    		SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
    		outputSpeech.setSsml(result.getSpeech());
    		return SpeechletResponse.newTellResponse(outputSpeech);
    	}
    	else {
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText(result.getSpeech());
			return SpeechletResponse.newTellResponse(outputSpeech);	
    	}
    }


	public static SpeechletResponse makeAskResponse(IntentResult result, Session session, String reprompt) {
		for (Map.Entry<String, String> entry : result.getIntentSlotMap().entrySet()) {
			if (entry.getValue() != null) {
				session.setAttribute(entry.getKey(), entry.getValue());
			}
		}
		session.setAttribute(InterfaceIntent.ATTR_LAST_INTENT, result.getName());

		OutputSpeech outputSpeech = null;
    	if (result.isSsml()) {
    		outputSpeech = new SsmlOutputSpeech();
    		((SsmlOutputSpeech) outputSpeech).setSsml(result.getSpeech());
    	}
    	else {
			outputSpeech = new PlainTextOutputSpeech();
			((PlainTextOutputSpeech) outputSpeech).setText(result.getSpeech());
    	}

		Reprompt prompt = new Reprompt();
		PlainTextOutputSpeech promptSpeech = new PlainTextOutputSpeech();
		promptSpeech.setText(reprompt);
		prompt.setOutputSpeech(promptSpeech);
	   	
		result.setResult(IntentGetStockPrice.RESULT_INCOMPLETE);
		return SpeechletResponse.newAskResponse(outputSpeech, prompt);	    	
	}
}
