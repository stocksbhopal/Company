package com.sanjoyghosh.company.earnings.intent;

import java.time.LocalDate;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.sanjoyghosh.company.utils.AlexaDateUtils;
import com.sanjoyghosh.company.utils.LocalDateRange;

public class IntentUtils {
	
	public static LocalDateRange getValidDateRange(IntentRequest request) {
		Intent intent = request.getIntent();
		Slot slot = intent.getSlot(InterfaceIntent.SLOT_DATE);
		if (slot == null) {
			return null;
		}
		String dateStr = slot.getValue();
		if (dateStr == null || dateStr.trim().length() == 0) {
			return null;
		}
		
		LocalDateRange localDateRange = AlexaDateUtils.getLocalDateRange(dateStr);
		if (localDateRange.getEndDate().isBefore(LocalDate.now())) {
			return null;
		}
		if (localDateRange.getEndDate().isAfter(LocalDate.now().plusDays(30))) {
			return null;
		}
		return localDateRange;
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
    	symbol = symbol == null ? null : symbol.trim();
    	companyOrSymbol = companyOrSymbol == null ? null : companyOrSymbol.trim();
    	
    	CompanyOrSymbol cos = new CompanyOrSymbol(companyOrSymbol, symbol);
    	return cos;
    }
}
