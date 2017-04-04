package com.sanjoyghosh.company.earnings.intent;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.sanjoyghosh.company.logs.CloudWatchLogger;
import com.sanjoyghosh.company.logs.CloudWatchLoggerIntentResult;
import com.sanjoyghosh.company.utils.AlexaDateUtils;
import com.sanjoyghosh.company.utils.LocalDateRange;

public class IntentUtils {
	
    private static final Logger logger = Logger.getLogger(IntentUtils.class.getName());

	private static void logSlotValue(String intentName, String slot, String slotValue) {
		logger.log(Level.INFO, intentName + " got for slot: " + slot + ", value: " + slotValue);
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
		if (localDateRange.getEndDate().isBefore(LocalDate.now().minusDays(1L))) {
			return null;
		}
		if (localDateRange.getEndDate().isAfter(LocalDate.now().plusDays(30))) {
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
    	String resultText,
    	AllSlotValues slotValues,
    	String speechText, 
    	String loggerMessage) {
    	
		String juliLoggerMessage = intentName + ": " + loggerMessage;
    	CloudWatchLoggerIntentResult loggerResult = new CloudWatchLoggerIntentResult(alexaUserId, intentName, result, 
    		resultText, slotValues == null ? null : slotValues.toKeyValuePairList(), new Date());
    	CloudWatchLogger.getInstance().addLogEvent(loggerResult, juliLoggerMessage);

		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(speechText);
		return SpeechletResponse.newTellResponse(outputSpeech);			
    }


    public static SpeechletResponse makeTellResponse(
    	String alexaUserId, 
    	String intentName, 
    	int result, 
    	String resultText,
    	AllSlotValues slotValues,
    	String speechText, 
    	String loggerMessage,
    	Throwable e) {
    	
		String juliLoggerMessage = intentName + ": " + loggerMessage;
    	CloudWatchLoggerIntentResult loggerResult = new CloudWatchLoggerIntentResult(alexaUserId, intentName, result, 
    		resultText, slotValues == null ? null : slotValues.toKeyValuePairList(), new Date());
    	CloudWatchLogger.getInstance().addLogEvent(loggerResult, juliLoggerMessage, e);

		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(speechText);
		return SpeechletResponse.newTellResponse(outputSpeech);			
    }
}
