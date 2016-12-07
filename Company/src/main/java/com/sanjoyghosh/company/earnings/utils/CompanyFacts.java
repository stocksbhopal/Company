package com.sanjoyghosh.company.earnings.utils;

public class CompanyFacts {

	// All Strings should be lower case
	private String	name;		
	private String	symbol;
	private String 	fullName;
	
	
	public CompanyFacts(String name, String symbol, String fullName) {
		this.name = name.toLowerCase();
		this.symbol = symbol.toLowerCase();
		this.fullName = fullName;
	}


	public String getName() {
		return name;
	}


	public String getSymbol() {
		return symbol;
	}


	public String getFullName() {
		return fullName;
	}
}
