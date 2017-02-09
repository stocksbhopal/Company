package com.sanjoyghosh.company.earnings.utils;

public class CompanyFacts {

	// All Strings should be lower case
	private String	symbol;
	private String 	fullName;
	
	
	public CompanyFacts(String symbol, String fullName) {
		this.symbol = symbol.toLowerCase();
		this.fullName = fullName;
	}


	public String getSymbol() {
		return symbol;
	}


	public String getFullName() {
		return fullName;
	}
}
