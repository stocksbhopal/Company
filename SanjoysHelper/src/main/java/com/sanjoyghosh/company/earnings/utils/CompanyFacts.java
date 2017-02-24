package com.sanjoyghosh.company.earnings.utils;

public class CompanyFacts {

	// All Strings should be lower case
	private String	symbol;
	private String 	speechName;
	
	
	public CompanyFacts(String symbol, String speechName) {
		this.symbol = symbol.toLowerCase();
		this.speechName = speechName;
	}


	public String getSymbol() {
		return symbol;
	}


	public String getSpeechName() {
		return speechName;
	}
}
