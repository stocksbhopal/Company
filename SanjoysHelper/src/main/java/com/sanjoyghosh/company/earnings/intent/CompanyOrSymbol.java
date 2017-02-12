package com.sanjoyghosh.company.earnings.intent;

public class CompanyOrSymbol {

	private String companyOrSymbol;		// Prefix of the name of the company or the ticker symbol.
	private String symbol;				// Ticker symbol for the company.
	
	
	public CompanyOrSymbol(String companyOrSymbol, String symbol) {
		this.companyOrSymbol = companyOrSymbol;
		this.symbol = symbol;
	}


	public String getCompanyOrSymbol() {
		return companyOrSymbol;
	}


	public String getSymbol() {
		return symbol;
	}
	
	
	public boolean isEmpty() {
		return (companyOrSymbol == null || companyOrSymbol.length() == 0) && (symbol == null || symbol.length() == 0);
	}


	@Override
	public String toString() {
		if (isEmpty()) {
			return "";
		}
		else if ((companyOrSymbol != null && companyOrSymbol.length() > 0) && (symbol != null && symbol.length() > 0)) {
			return companyOrSymbol + " or " + symbol;
		}
		else if (companyOrSymbol != null && companyOrSymbol.length() > 0) {
			return companyOrSymbol;
		}
		else {
			return symbol;
		}
	}
}
