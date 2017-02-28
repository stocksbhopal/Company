package com.sanjoyghosh.company.earnings.intent;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.sanjoyghosh.company.utils.AlexaDateUtils;
import com.sanjoyghosh.company.utils.LocalDateRange;

public class IntentUtils {
	
    private static final Logger logger = Logger.getLogger(IntentUtils.class.getPackage().getName());

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
	

    public static CompanyOrSymbol getCompanyOrSymbol(IntentRequest request) {
    	String symbol = "";
    	String companyOrSymbol = "";
    	
    	Intent intent = request.getIntent();
    	if (intent.getSlot(InterfaceIntent.SLOT_COMPANY) != null && 
    		intent.getSlot(InterfaceIntent.SLOT_COMPANY).getValue() != null && 
    		intent.getSlot(InterfaceIntent.SLOT_COMPANY).getValue().trim().length() > 0) {
    		companyOrSymbol = intent.getSlot(InterfaceIntent.SLOT_COMPANY).getValue();
    	}
    	else {
    		if (intent.getSlot(InterfaceIntent.SLOT_SPELLING_SIX) != null && 
    			intent.getSlot(InterfaceIntent.SLOT_SPELLING_SIX).getValue() != null &&
    			intent.getSlot(InterfaceIntent.SLOT_SPELLING_SIX).getValue().trim().length() > 0) {
    			companyOrSymbol = intent.getSlot(InterfaceIntent.SLOT_SPELLING_SIX).getValue().trim();
    			symbol = intent.getSlot(InterfaceIntent.SLOT_SPELLING_SIX).getValue().trim().substring(0, 1) + symbol;
    		}
    		if (intent.getSlot(InterfaceIntent.SLOT_SPELLING_FIVE) != null && 
    			intent.getSlot(InterfaceIntent.SLOT_SPELLING_FIVE).getValue() != null &&
    			intent.getSlot(InterfaceIntent.SLOT_SPELLING_FIVE).getValue().trim().length() > 0) {
    			companyOrSymbol = intent.getSlot(InterfaceIntent.SLOT_SPELLING_FIVE).getValue().trim() + " " + companyOrSymbol;
    			symbol = intent.getSlot(InterfaceIntent.SLOT_SPELLING_FIVE).getValue().trim().substring(0, 1) + symbol;
    		}
    		if (intent.getSlot(InterfaceIntent.SLOT_SPELLING_FOUR) != null && 
    			intent.getSlot(InterfaceIntent.SLOT_SPELLING_FOUR).getValue() != null &&
    			intent.getSlot(InterfaceIntent.SLOT_SPELLING_FOUR).getValue().trim().length() > 0) {
    			companyOrSymbol = intent.getSlot(InterfaceIntent.SLOT_SPELLING_FOUR).getValue().trim() + " " + companyOrSymbol;
    			symbol = intent.getSlot(InterfaceIntent.SLOT_SPELLING_FOUR).getValue().trim().substring(0, 1) + symbol;
    		}
    		if (intent.getSlot(InterfaceIntent.SLOT_SPELLING_THREE) != null && 
    			intent.getSlot(InterfaceIntent.SLOT_SPELLING_THREE).getValue() != null &&
    			intent.getSlot(InterfaceIntent.SLOT_SPELLING_THREE).getValue().trim().length() > 0) {
    			companyOrSymbol = intent.getSlot(InterfaceIntent.SLOT_SPELLING_THREE).getValue().trim() + " " + companyOrSymbol;
    			symbol = intent.getSlot(InterfaceIntent.SLOT_SPELLING_THREE).getValue().trim().substring(0, 1) + symbol;
    		}
    		if (intent.getSlot(InterfaceIntent.SLOT_SPELLING_TWO) != null && 
    			intent.getSlot(InterfaceIntent.SLOT_SPELLING_TWO).getValue() != null &&
    			intent.getSlot(InterfaceIntent.SLOT_SPELLING_TWO).getValue().trim().length() > 0) {
    			companyOrSymbol = intent.getSlot(InterfaceIntent.SLOT_SPELLING_TWO).getValue().trim() + " " + companyOrSymbol;
    			symbol = intent.getSlot(InterfaceIntent.SLOT_SPELLING_TWO).getValue().trim().substring(0, 1) + symbol;
    		}
    		if (intent.getSlot(InterfaceIntent.SLOT_SPELLING_ONE) != null && 
    			intent.getSlot(InterfaceIntent.SLOT_SPELLING_ONE).getValue() != null &&
    			intent.getSlot(InterfaceIntent.SLOT_SPELLING_ONE).getValue().trim().length() > 0) {
    			companyOrSymbol = intent.getSlot(InterfaceIntent.SLOT_SPELLING_ONE).getValue().trim() + " " + companyOrSymbol;
    			symbol = intent.getSlot(InterfaceIntent.SLOT_SPELLING_ONE).getValue().trim().substring(0, 1) + symbol;
    		}
    	}
    	symbol = symbol == null ? null : removeTrailingWord(symbol.trim());
    	companyOrSymbol = companyOrSymbol == null ? null : removeTrailingWord(companyOrSymbol.trim());
    	
    	CompanyOrSymbol cos = new CompanyOrSymbol(companyOrSymbol, symbol);
    	return cos;
    }
}
