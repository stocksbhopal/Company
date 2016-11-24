package com.sanjoyghosh.company.earnings.utils;

public class CompanyFacts {

	// All Strings should be lower case
	private String	name;		
	private String	symbol;
	
	
	public CompanyFacts(String name, String symbol) {
		this.name = name.toLowerCase();
		this.symbol = symbol.toLowerCase();
	}


	public String getName() {
		return name;
	}


	public String getSymbol() {
		return symbol;
	}
}
