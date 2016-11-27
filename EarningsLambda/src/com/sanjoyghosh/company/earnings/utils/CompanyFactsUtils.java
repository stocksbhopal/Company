package com.sanjoyghosh.company.earnings.utils;

import java.util.HashMap;

public class CompanyFactsUtils {

	private static final HashMap<String, CompanyFacts> companyFactsByNameMap = new HashMap<>();
	static {
		addCompany("acacia", "acia");
		addCompany("adobe", "adbe");
		addCompany("agilent", "a");
		addCompany("alibaba", "baba");
		addCompany("amazon", "amzn");
		addCompany("ambarella", "amba");
		addCompany("american towers", "amt");
		addCompany("analog devices", "adi");
		addCompany("apple", "aapl");
		addCompany("applied materials", "amat");
		addCompany("arm holdings", "armh");
		addCompany("autodesk", "adsk");
		addCompany("baidu", "bidu");
		addCompany("bazaarvoice", "bv");
		addCompany("big lots", "big");
		addCompany("biomarin", "bmrn");
		addCompany("boston scientific", "bsx");
		addCompany("box", "box");
		addCompany("broadcom", "avgo");
		addCompany("burlington", "burl");
		addCompany("carnival", "ccl");
		addCompany("childrens place", "plce");
		addCompany("ciena", "cien");
		addCompany("comcast", "cmcsa");
		addCompany("danaher", "dhr");
		addCompany("dollar general", "dg");
		addCompany("edwards", "ew");
		addCompany("electronic arts", "ea");
		addCompany("equinix", "eqix");
		addCompany("facebook", "fb");
		addCompany("fedex", "fdx");
		addCompany("foot locker", "fl");
		addCompany("gigamon", "gimo");
		addCompany("glaukos", "gkos");
		addCompany("goldman sachs", "gs");
		addCompany("google", "goog");
		addCompany("guidewire", "gwre");
		addCompany("home depot", "hd");
		addCompany("hubspot", "hubs");
		addCompany("inphi", "iphi");
		addCompany("intuitive surgical", "isrg");
		addCompany("irythm", "irth");
		addCompany("kraft heinz", "khc");
		addCompany("lam research", "lrcx");
		addCompany("mallinckrodt", "mnk");
		addCompany("mastercard", "ma");
		addCompany("medidata", "mdso");
		addCompany("mentor graphics", "ment");
		addCompany("merck", "mrk");
		addCompany("microchip", "mchp");
		addCompany("microsoft", "msft");
		addCompany("net ease", "ntes");
		addCompany("netflix", "nflx");
		addCompany("nevro", "nvro");
		addCompany("nutanix", "ntnx");
		addCompany("nvidia", "nvda");
		addCompany("ooma", "ooma");
		addCompany("palo alto networks", "panw");
		addCompany("penumbra", "pen");
		addCompany("planet fitness", "plnt");
		addCompany("priceline", "pcln");
		addCompany("proof point", "pfpt");
		addCompany("ross stores", "rost");
		addCompany("salesforce", "crm");
		addCompany("service now", "now");
		addCompany("shopify", "shop");
		addCompany("splunk", "splk");
		addCompany("starbucks", "sbux");
		addCompany("synchross", "sncr");
		addCompany("synopsys", "snps");
		addCompany("take two", "ttwo");
		addCompany("target", "tgt");
		addCompany("texas instruments", "txn");
		addCompany("thermo fisher", "tmo");
		addCompany("tiffany", "tif");
		addCompany("toll brothers", "tol");
		addCompany("ulta salon", "ulta");
		addCompany("vail resorts", "mtn");
		addCompany("veeva", "veev");
		addCompany("verint", "vrnt");
		addCompany("visa", "v");
		addCompany("weibo", "wb");
		addCompany("wix", "wix");
		addCompany("work day", "wday");
		addCompany("right medical", "wmgi");
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
