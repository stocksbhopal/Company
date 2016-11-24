package com.sanjoyghosh.company.earnings.utils;

import java.util.HashMap;

public class CompanyFactsUtils {

	private static final HashMap<String, CompanyFacts> companyFactsByNameMap = new HashMap<>();
	static {
		addCompany("agilent", "a");
		addCompany("adobe", "adbe");
		addCompany("amazon", "amzn");
		addCompany("ambarella", "amba");
		addCompany("american towers", "amt");
		addCompany("analog devices", "adi");
		addCompany("apple", "aapl");
		addCompany("applied materials", "amat");
		addCompany("arm holdings", "armh");
		addCompany("google", "goog");
		addCompany("netflix", "nflx");
		addCompany("nvidia", "nvda");
		addCompany("yahoo", "yhoo");
	}
	

	private static void addCompany(String name, String symbol) {
		companyFactsByNameMap.put(name, new CompanyFacts(name, symbol));
	}
	
	
	public static CompanyFacts getCompanyFactsForName(String name) {
		return companyFactsByNameMap.get(name.toLowerCase());
	}
}
