package com.sanjoyghosh.company.earnings.intent;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;

public class IntentUtils {

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
    	if (cos.isEmpty()) {
    		System.out.println(intent.getName() + " no company or symbol provided");
    	}
    	else {
    		System.out.println(intent.getName() + " invoked for company or symbol: " + cos);
    	}
    	return cos;
    }
}
