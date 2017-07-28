package com.sanjoyghosh.company.utils;

import com.sanjoyghosh.company.db.model.Company;

public class Utils {

	public static int toInt(Double valueDouble) {
		if (valueDouble == null) {
			return 0;
		}
		return (int)(double)valueDouble;
	}

	public static int toInt(Integer valueInteger) {
		if (valueInteger == null) {
			return 0;
		}
		return valueInteger;
	}
	
	public static double toDouble(Double valueDouble) {
		if (valueDouble == null) {
			return 0.00D;
		}
		return valueDouble;
	}
	
	public static boolean toBoolean(Boolean valueBoolean) {
		if (valueBoolean == null) {
			return false;
		}
		return valueBoolean;
	}
	
	public static String toReutersSymbol(Company company) {
		if (company == null) {
			return null;
		}
		if (company.getExchange().equals("nasdaq")) {
			return company.getSymbol() + "." + "O";
		}
		else if (company.getExchange().equals("nyse")) {
			return company.getSymbol() + "." + "N";
		}
		return null;
	}

	public static String toGoogleFinanceSymbol(Company company) {
		if (company == null) {
			return null;
		}
		if (company.getExchange().equals("nasdaq")) {
			return "NASDAQ:" + company.getSymbol();
		}
		else if (company.getExchange().equals("nyse")) {
			return "NYSE:" + company.getSymbol();
		}
		return null;
	}
}
