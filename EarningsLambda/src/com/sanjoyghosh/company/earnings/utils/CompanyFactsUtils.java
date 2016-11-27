package com.sanjoyghosh.company.earnings.utils;

import java.util.HashMap;

public class CompanyFactsUtils {

	private static final HashMap<String, CompanyFacts> companyFactsByNameMap = new HashMap<>();
	static {
		addCompany("acacia", "acia", "Acacia Communications");
		addCompany("adobe", "adbe", "Adobe Systems");
		addCompany("agilent", "a", "Agilent Technologies");
		addCompany("alibaba", "baba", "Alibaba Group");
		addCompany("amazon", "amzn", "Amazon.com");
		addCompany("ambarella", "amba", "Ambarella");
		addCompany("american towers", "amt", "Applied Materials");
		addCompany("analog devices", "adi", "Analog Devices");
		addCompany("apple", "aapl", "Apple");
		addCompany("applied materials", "amat", "Applied Materials");
		addCompany("arm holdings", "armh", "NOT AVAILABLE");
		addCompany("autodesk", "adsk", "Autodesk");
		addCompany("baidu", "bidu", "Baidu");
		addCompany("bazaarvoice", "bv", "Bazaarvoice");
		addCompany("big lots", "big", "Big Lots");
		addCompany("biomarin", "bmrn", "BioMarin Pharmaceutical");
		addCompany("boston scientific", "bsx", "Boston Scientific");
		addCompany("box", "box", "Box");
		addCompany("broadcom", "avgo", "Broadcom");
		addCompany("burlington", "burl", "Burlington Stores");
		addCompany("carnival", "ccl", "Carnival");
		addCompany("childrens place", "plce", "Children's Place");
		addCompany("ciena", "cien", "Ciena");
		addCompany("comcast", "cmcsa", "Comcast");
		addCompany("danaher", "dhr", "Danaher");
		addCompany("dollar general", "dg", "Dollar General");
		addCompany("edwards", "ew", "Edwards Lifesciences");
		addCompany("electronic arts", "ea", "Electronic Arts");
		addCompany("equinix", "eqix", "Equinix");
		addCompany("facebook", "fb", "Facebook");
		addCompany("fedex", "fdx", "FedEx");
		addCompany("foot locker", "fl", "Foot Locker");
		addCompany("gigamon", "gimo", "Gigamon");
		addCompany("glaukos", "gkos", "Glaukos");
		addCompany("goldman sachs", "gs", "Goldman Sachs Group");
		addCompany("google", "goog", "Alphabet Inc");
		addCompany("guidewire", "gwre", "Guidewire Software");
		addCompany("home depot", "hd", "Home Depot");
		addCompany("hubspot", "hubs", "HubSpot");
		addCompany("inphi", "iphi", "Inphi");
		addCompany("intuitive surgical", "isrg", "Intuitive Surgical");
		addCompany("irhythm", "irtc", "iRhythm Technologies");
		addCompany("kraft heinz", "khc", "Kraft Heinz");
		addCompany("lam research", "lrcx", "Lam Research");
		addCompany("mallinckrodt", "mnk", "Mallinckrodt");
		addCompany("mastercard", "ma", "Mastercard");
		addCompany("medidata", "mdso", "Medidata Solutions");
		addCompany("mentor graphics", "ment", "Mentor Graphics");
		addCompany("merck", "mrk", "Merck & Company");
		addCompany("microchip", "mchp", "Microchip Technology");
		addCompany("microsoft", "msft", "Microsoft");
		addCompany("net ease", "ntes", "NetEase");
		addCompany("netflix", "nflx", "Netflix");
		addCompany("nevro", "nvro", "Nevro");
		addCompany("nutanix", "ntnx", "Nutanix");
		addCompany("nvidia", "nvda", "NVIDIA");
		addCompany("ooma", "ooma", "Ooma");
		addCompany("palo alto networks", "panw", "Palo Alto Networks");
		addCompany("penumbra", "pen", "Penumbra");
		addCompany("planet fitness", "plnt", "Planet Fitness");
		addCompany("priceline", "pcln", "Priceline Group");
		addCompany("proof point", "pfpt", "Proofpoint");
		addCompany("ross stores", "rost", "Ross Stores");
		addCompany("salesforce", "crm", "Salesforce.com");
		addCompany("service now", "now", "ServiceNow");
		addCompany("shopify", "shop", "Shopify");
		addCompany("splunk", "splk", "Splunk");
		addCompany("starbucks", "sbux", "Starbucks");
		addCompany("synchross", "sncr", "Synchronoss Technologies");
		addCompany("synopsys", "snps", "Synopsys");
		addCompany("take two", "ttwo", "Take-Two Interactive Software");
		addCompany("target", "tgt", "Target");
		addCompany("texas instruments", "txn", "Texas Instruments");
		addCompany("thermo fisher", "tmo", "Thermo Fisher Scientific");
		addCompany("tiffany", "tif", "Tiffany & Co");
		addCompany("toll brothers", "tol", "Toll Brothers");
		addCompany("ulta salon", "ulta", "Ulta Salon, Cosmetics & Fragrance");
		addCompany("vail resorts", "mtn", "Vail Resorts");
		addCompany("veeva", "veev", "Veeva Systems");
		addCompany("verint", "vrnt", "Verint Systems");
		addCompany("visa", "v", "Visa");
		addCompany("weibo", "wb", "Weibo");
		addCompany("wix", "wix", "Wix.com");
		addCompany("work day", "wday", "Workday");
		addCompany("right medical", "wmgi", "Wright Medical Group");
		addCompany("yahoo", "yhoo", "Yahoo!");
		addCompany("yelp", "yelp", "Yelp");
		addCompany("zayo", "zayo", "Zayo Group Holdings");
		addCompany("zillow", "z", "Zillow Group");
	}
	

	private static void addCompany(String name, String symbol, String fullName) {
		companyFactsByNameMap.put(name, new CompanyFacts(name, symbol, fullName));
	}
	
	
	public static CompanyFacts getCompanyFactsForName(String name) {
		return companyFactsByNameMap.get(name.toLowerCase());
	}
}
