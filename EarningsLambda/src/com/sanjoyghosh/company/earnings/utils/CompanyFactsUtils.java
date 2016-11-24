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
		addCompany("baidu", "bidu");
		addCompany("biomarin", "bmrn");
		addCompany("boston scientific", "bsx");
		addCompany("broadcom", "avgo");
		addCompany("childrens place", "plce");
		addCompany("comcast", "cmcsa");
		addCompany("danaher", "dhr");
		addCompany("edwards", "ew");
		addCompany("equinix", "eqix");
		addCompany("facebook", "fb");
		addCompany("foot locker", "fl");
		addCompany("gigamon", "gimo");
		addCompany("goldman sachs", "gs");
		addCompany("google", "goog");
		addCompany("home depot", "hd");
		addCompany("inphi", "iphi");
		addCompany("irythm", "irth");
		addCompany("kraft heinz", "khc");
		addCompany("lam research", "lrcx");
		addCompany("medidata", "mdso");
		addCompany("mentor graphics", "ment");
		addCompany("merck", "mrk");
		addCompany("microchip", "mchp");
		addCompany("netflix", "nflx");
		addCompany("nvidia", "nvda");
		addCompany("palo alto networks", "panw");
		addCompany("planet fitness", "plnt");
		addCompany("priceline", "pcln");
		addCompany("proofpoint", "pfpt");
		addCompany("salesforce", "crm");
		addCompany("shopify", "shop");
		addCompany("starbucks", "sbux");
		addCompany("synchross", "sncr");
		addCompany("synopsys", "snps");
		addCompany("take two", "ttwo");
		addCompany("texas instruments", "txn");
		addCompany("thermo fisher", "tmo");
		addCompany("toll brothers", "tol");
		addCompany("veeva", "veev");
		addCompany("visa", "v");
		addCompany("wix", "wix");
		addCompany("yahoo", "yhoo");
		addCompany("yelp", "yelp");
		addCompany("zayo", "zayo");
		addCompany("zillow", "z");
	}
	

	private static void addCompany(String name, String symbol) {
		companyFactsByNameMap.put(name, new CompanyFacts(name, symbol));
	}
	
	
	public static CompanyFacts getCompanyFactsForName(String name) {
		return companyFactsByNameMap.get(name.toLowerCase());
	}
}
