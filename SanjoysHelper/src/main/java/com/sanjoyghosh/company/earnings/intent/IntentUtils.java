package com.sanjoyghosh.company.earnings.intent;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.sanjoyghosh.company.logs.CloudWatchLogger;
import com.sanjoyghosh.company.logs.CloudWatchLoggerIntentResult;
import com.sanjoyghosh.company.utils.AlexaDateUtils;
import com.sanjoyghosh.company.utils.KeyValuePair;
import com.sanjoyghosh.company.utils.LocalDateRange;

public class IntentUtils {
	
    private static final Logger logger = Logger.getLogger(IntentUtils.class.getName());

	private static void logSlotValue(String intentName, String slot, String slotValue) {
		logger.log(Level.INFO, intentName + " got for slot: " + slot + ", value: " + slotValue);
	}
	
	
	public static List<KeyValuePair> getSlotsFromIntent(IntentRequest request) {
		List<KeyValuePair> slots = new ArrayList<>();
		for (Entry<String, Slot> entry : request.getIntent().getSlots().entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue().getValue();
			value = value == null ? null : value.trim();
			if (value != null && value.length() > 0) {
				KeyValuePair pair = new KeyValuePair(key, value);
				slots.add(pair);
			}
		}
		return slots;
	}
	

	public static List<KeyValuePair> getAttributesFromSession(Session session) {
		List<KeyValuePair> attributes = new ArrayList<>();
		for (Entry<String, Object> entry : session.getAttributes().entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue().toString();
			value = value == null ? null : value.trim();
			if (value != null && value.length() > 0) {
				KeyValuePair pair = new KeyValuePair(key, value);
				attributes.add(pair);
			}
		}
		return attributes;
	}

	
	public static LocalDateRange getValidDateRange(IntentRequest request) {
		Intent intent = request.getIntent();
		Slot slot = intent.getSlot(InterfaceIntent.SLOT_DATE);
		if (slot == null) {
			logSlotValue(intent.getName(), InterfaceIntent.SLOT_DATE, "NO_VALUE");
			return null;
		}
		String dateStr = slot.getValue();
		if (dateStr == null || dateStr.trim().length() == 0) {
			logSlotValue(intent.getName(), InterfaceIntent.SLOT_DATE, "NULL_OR_EMPTY:SET_TO_TODAY");
			LocalDate localDate = LocalDate.now(ZoneId.of("America/New_York"));
			LocalDateRange dateRange = new LocalDateRange(localDate, localDate, 1);
			return dateRange;
		}
		
		logSlotValue(intent.getName(), InterfaceIntent.SLOT_DATE, dateStr);
		LocalDateRange localDateRange = AlexaDateUtils.getLocalDateRange(dateStr);
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
	

    public static boolean getCompanyOrSymbol(IntentRequest request, Session session, AllSlotValues slotValues) {
    	String symbol = (String) session.getAttribute(InterfaceIntent.ATTR_SYMBOL);
    	if (symbol != null) {
    		slotValues.setCompanyOrSymbol(symbol);
    		slotValues.setCompanyOrSymbolSpelt("");
    		return true;
    	}
    	
       	Intent intent = request.getIntent();
    	if (intent.getSlot(InterfaceIntent.SLOT_COMPANY) == null ||
    		intent.getSlot(InterfaceIntent.SLOT_COMPANY).getValue() == null ||
    		intent.getSlot(InterfaceIntent.SLOT_COMPANY).getValue().trim().length() == 0) {
    		return false;
    	}
    	
    	String companyOrSymbol = "";
    	String companyOrSymbolSpelt = "";
		companyOrSymbol = intent.getSlot(InterfaceIntent.SLOT_COMPANY).getValue();
    	
    	// Take the apostrophe out for McDonald's and Dick's Sporting Goods
    	companyOrSymbol = removeTrailingWord(companyOrSymbol.trim().replaceAll("'", "")).trim();
    	
    	String[] pieces = companyOrSymbol.split(" ");
    	for (String piece : pieces) {
    		companyOrSymbolSpelt += piece.charAt(0);
    	}
    	companyOrSymbolSpelt = companyOrSymbolSpelt.trim();

    	slotValues.setCompanyOrSymbol(companyOrSymbol);
    	slotValues.setCompanyOrSymbolSpelt(companyOrSymbolSpelt);
       	return true;
    }
    
    
    public static boolean getQuantity(IntentRequest request, Session session, AllSlotValues slotValues) {
    	Double quantity = (Double) session.getAttribute(InterfaceIntent.ATTR_QUANTITY);
    	if (quantity != null) {
    		slotValues.setQuantity(quantity);
    		return true;
    	}
    	
    	Intent intent = request.getIntent();
    	if (intent.getSlot(InterfaceIntent.SLOT_QUANTITY) == null ||
        	intent.getSlot(InterfaceIntent.SLOT_QUANTITY).getValue() == null ||
        	intent.getSlot(InterfaceIntent.SLOT_QUANTITY).getValue().trim().length() == 0) {
    		return false;
        }

    	String quantityStr = null;
    	quantityStr = intent.getSlot(InterfaceIntent.SLOT_QUANTITY).getValue();
    	if (quantityStr == null) {
    		return false;
    	}
    	
    	try {
    		quantity = Double.parseDouble(quantityStr);
    		slotValues.setQuantity(quantity);
    		return true;
    	}
    	catch (Exception e) {
    		logger.log(Level.SEVERE, intent.getName() + " given bad value for quantity: " + quantityStr, e);
    	}
    	return false;
    }
    
    
    public static SpeechletResponse makeTellResponse(
    	String alexaUserId, 
    	String intentName, 
    	int result, 
    	AllSlotValues slotValues,
    	String speechText) {
    	
		return makeTellResponse(alexaUserId, intentName, result, "", slotValues, speechText);			
    }

    
    public static SpeechletResponse makeAskResponse(
    	String alexaUserId, 
    	String intentName, 
    	int result, 
    	AllSlotValues slotValues,
    	String speechText) {
    	
    	return makeAskResponse(alexaUserId, intentName, result, "", slotValues, speechText);
    }
    
    
    public static SpeechletResponse makeTellResponse(
    	String alexaUserId, 
    	String intentName, 
    	int result, 
    	String resultText,
    	AllSlotValues slotValues,
    	String speechText) {
    	
		String juliLoggerMessage = intentName + ": " + speechText;
    	CloudWatchLoggerIntentResult loggerResult = new CloudWatchLoggerIntentResult(alexaUserId, intentName, result, 
    		resultText, slotValues == null ? null : slotValues.toKeyValuePairList(), new Date());
    	CloudWatchLogger.getInstance().addLogEvent(loggerResult, juliLoggerMessage);

		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(speechText);
		return SpeechletResponse.newTellResponse(outputSpeech);			
    }


    public static SpeechletResponse makeAskResponse(
    	String alexaUserId, 
    	String intentName, 
    	int result, 
    	String resultText,
    	AllSlotValues slotValues,
    	String speechText) {
    	
		String juliLoggerMessage = intentName + ": " + speechText;
    	CloudWatchLoggerIntentResult loggerResult = new CloudWatchLoggerIntentResult(alexaUserId, intentName, result, 
    		resultText, slotValues == null ? null : slotValues.toKeyValuePairList(), new Date());
    	CloudWatchLogger.getInstance().addLogEvent(loggerResult, juliLoggerMessage);

		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(speechText);
		
		Reprompt reprompt = new Reprompt();
		PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
		repromptSpeech.setText("Sorry, " + speechText);
		reprompt.setOutputSpeech(repromptSpeech);

		return SpeechletResponse.newAskResponse(outputSpeech, reprompt);			
    }

    
    public static SpeechletResponse makeTellResponse(
    	String alexaUserId, 
    	String intentName, 
    	int result, 
    	AllSlotValues slotValues,
    	String speechText,
    	Throwable e) {
    	
		return makeTellResponse(alexaUserId, intentName, result, "", slotValues, speechText, e);			
    }

    
    public static SpeechletResponse makeTellResponse(
    	String alexaUserId, 
    	String intentName, 
    	int result, 
    	String resultText,
    	AllSlotValues slotValues,
    	String speechText,
    	Throwable e) {
    	
		String juliLoggerMessage = intentName + ": " + speechText;
    	CloudWatchLoggerIntentResult loggerResult = new CloudWatchLoggerIntentResult(alexaUserId, intentName, result, 
    		resultText, slotValues == null ? null : slotValues.toKeyValuePairList(), new Date());
    	CloudWatchLogger.getInstance().addLogEvent(loggerResult, juliLoggerMessage, e);

		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(speechText);
		return SpeechletResponse.newTellResponse(outputSpeech);			
    }
    
    
	public static SpeechletResponse respondWithQuestion(IntentResult intentResult, IntentRequest request, Session session, 
		String questionPrefix, String repromptSuffix) {
		
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(questionPrefix + "what company?  Tell me the name or symbol.");

		Reprompt reprompt = new Reprompt();
		PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
		repromptSpeech.setText("Sorry, need the name or symbol to get" + repromptSuffix);
		reprompt.setOutputSpeech(repromptSpeech);
	   	
		intentResult.setResult(IntentGetStockPrice.RESULT_INCOMPLETE);
		logger.log(Level.INFO, request.getIntent().getName() + ": user did not provide name of company.");
		return SpeechletResponse.newAskResponse(outputSpeech, reprompt);	    	
	}
}
