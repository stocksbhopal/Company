package com.sanjoyghosh.company.earnings.intent;

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
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SsmlOutputSpeech;
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
	
	
	private static String removeTrailingWord(String name) {
		if (name == null || name.length() == 0) {
			return name;
		}
		
		String nameLower = name.toLowerCase().trim();
		if (nameLower.endsWith("inc")) {
			return name.substring(0, name.length() - "inc".length()).trim();
		}
		if (nameLower.endsWith("corporation")) {
			return name.substring(0, name.length() - "corporation".length()).trim();
		}
		if (nameLower.endsWith("ink")) {
			return name.substring(0, name.length() - "ink".length()).trim();
		}
		if (nameLower.endsWith("stock")) {
			return name.substring(0, name.length() - "stock".length()).trim();
		}
		if (nameLower.endsWith("stocks")) {
			return name.substring(0, name.length() - "stocks".length()).trim();
			
		}
		if (nameLower.endsWith("share")) {
			return name.substring(0, name.length() - "share".length()).trim();
			
		}
		if (nameLower.endsWith("shares")) {
			return name.substring(0, name.length() - "shares".length()).trim();			
		}
		if (nameLower.endsWith("headlines")) {
			return name.substring(0, name.length() - "headlines".length()).trim();			
		}
		if (nameLower.endsWith("news")) {
			return name.substring(0, name.length() - "news".length()).trim();			
		}
		return name;
	}
	

    public static boolean getCompanyOrSymbol(IntentResult result) {
    	if (result.getSlotValues().getCompany() != null) {
    		return true;
    	}
    	
    	String companyOrSymbol = result.getIntentSlotMap().get(InterfaceIntent.SLOT_COMPANY);
    	if (companyOrSymbol == null) {
    		return false;
    	}
    	
    	// Take the apostrophe out for McDonald's and Dick's Sporting Goods
    	companyOrSymbol = removeTrailingWord(companyOrSymbol.trim().replaceAll("'", "")).trim();
    	
    	String companyOrSymbolSpelt = "";
    	String[] pieces = companyOrSymbol.split(" ");
    	for (String piece : pieces) {
    		companyOrSymbolSpelt += piece.charAt(0);
    	}
    	companyOrSymbolSpelt = companyOrSymbolSpelt.trim();

    	result.getSlotValues().setCompanyOrSymbol(companyOrSymbol);
    	result.getSlotValues().setCompanyOrSymbolSpelt(companyOrSymbolSpelt);
       	return true;
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


	public static SpeechletResponse makeAskResponse(IntentResult result, Session session, String question, String reprompt) {
		logger.log(Level.INFO, result.getName() + ": " + question + "; " + reprompt);
		
		for (Map.Entry<String, String> entry : result.getIntentSlotMap().entrySet()) {
			session.setAttribute(entry.getKey(), entry.getValue());
		}
		session.setAttribute(InterfaceIntent.ATTR_LAST_INTENT, result.getName());
		session.setAttribute(InterfaceIntent.ATTR_ALL_SLOT_VALUES,  result.getSlotValues());

		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(question);

		Reprompt prompt = new Reprompt();
		PlainTextOutputSpeech promptSpeech = new PlainTextOutputSpeech();
		promptSpeech.setText(reprompt);
		prompt.setOutputSpeech(promptSpeech);
	   	
		result.setResult(IntentGetStockPrice.RESULT_INCOMPLETE);
		return SpeechletResponse.newAskResponse(outputSpeech, prompt);	    	
	}
}
