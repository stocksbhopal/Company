package com.sanjoyghosh.company.earnings.utils;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;

import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.utils.StringUtils;

public class CompanyFactsUtils {

	private static final HashMap<String, CompanyFacts> companyFactsByNameMap = new HashMap<>();
	private static final HashMap<String, CompanyFacts> companyFactsBySymbolMap = new HashMap<>();
	static {
		addCompany("acacia", "ACIA", "Acacia Communications");
		addCompany("adobe", "ADBE", "Adobe Systems");
		addCompany("agilent", "A", "Agilent Technologies");
		addCompany("alibaba", "BABA", "Alibaba Group");
		addCompany("amazon", "AMZN", "Amazon.com");
		addCompany("ambarella", "AMBA", "Ambarella");
		addCompany("american towers", "AMT", "Applied Materials");
		addCompany("analog devices", "ADI", "Analog Devices");
		addCompany("apple", "AAPL", "Apple");
		addCompany("applied materials", "AMAT", "Applied Materials");
		addCompany("arm holdings", "ARMH", "NOT AVAILABLE");
		addCompany("autodesk", "ADSK", "Autodesk");
		addCompany("baidu", "BIDU", "Baidu");
		addCompany("bazaarvoice", "BV", "Bazaarvoice");
		addCompany("big lots", "BIG", "Big Lots");
		addCompany("biomarin", "BMRN", "BioMarin Pharmaceutical");
		addCompany("boston scientific", "BSX", "Boston Scientific");
		addCompany("box", "BOX", "Box");
		addCompany("broadcom", "AVGO", "Broadcom");
		addCompany("burlington", "BURL", "Burlington Stores");
		addCompany("carnival", "CCL", "Carnival");
		addCompany("childrens place", "PLCE", "Children's Place");
		addCompany("ciena", "CIEN", "Ciena");
		addCompany("comcast", "CMCSA", "Comcast");
		addCompany("cost co", "COST", "Costco Wholesale");
		addCompany("danaher", "DHR", "Danaher");
		addCompany("dollar general", "DG", "Dollar General");
		addCompany("edwards", "EW", "Edwards Lifesciences");
		addCompany("electronic arts", "EA", "Electronic Arts");
		addCompany("equinix", "EQIX", "Equinix");
		addCompany("facebook", "FB", "Facebook");
		addCompany("fedex", "FDX", "FedEx");
		addCompany("foot locker", "FL", "Foot Locker");
		addCompany("gigamon", "GIMO", "Gigamon");
		addCompany("glaukos", "GKOS", "Glaukos");
		addCompany("goldman sachs", "GS", "Goldman Sachs Group");
		addCompany("google", "GOOG", "Alphabet Inc");
		addCompany("guidewire", "GWRE", "Guidewire Software");
		addCompany("home depot", "HD", "Home Depot");
		addCompany("hubspot", "HUBS", "HubSpot");
		addCompany("inphi", "IPHI", "Inphi");
		addCompany("intuitive surgical", "ISRG", "Intuitive Surgical");
		addCompany("irhythm", "IRTC", "iRhythm Technologies");
		addCompany("kraft heinz", "KHC", "Kraft Heinz");
		addCompany("lam research", "LRCX", "Lam Research");
		addCompany("mallinckrodt", "MNK", "Mallinckrodt");
		addCompany("mastercard", "MA", "Mastercard");
		addCompany("medidata", "MDSO", "Medidata Solutions");
		addCompany("mentor graphics", "MENT", "Mentor Graphics");
		addCompany("merck", "MRK", "Merck & Company");
		addCompany("microchip", "MCHP", "Microchip Technology");
		addCompany("microsoft", "MSFT", "Microsoft");
		addCompany("net ease", "NTES", "NetEase");
		addCompany("netflix", "NFLX", "Netflix");
		addCompany("nevro", "NVRO", "Nevro");
		addCompany("nutanix", "NTNX", "Nutanix");
		addCompany("nvidia", "NVDA", "NVIDIA");
		addCompany("ooma", "OOMA", "Ooma");
		addCompany("palo alto networks", "PANW", "Palo Alto Networks");
		addCompany("penumbra", "PEN", "Penumbra");
		addCompany("planet fitness", "PLNT", "Planet Fitness");
		addCompany("priceline", "PCLN", "Priceline Group");
		addCompany("proof point", "PFPT", "Proofpoint");
		addCompany("ross stores", "ROST", "Ross Stores");
		addCompany("salesforce", "CRM", "Salesforce.com");
		addCompany("service now", "NOW", "ServiceNow");
		addCompany("shopify", "SHOP", "Shopify");
		addCompany("splunk", "SPLK", "Splunk");
		addCompany("starbucks", "SBUX", "Starbucks");
		addCompany("synchross", "SNCR", "Synchronoss Technologies");
		addCompany("synopsys", "SNPS", "Synopsys");
		addCompany("take two", "TTWO", "Take-Two Interactive Software");
		addCompany("target", "TGT", "Target");
		addCompany("texas instruments", "TXN", "Texas Instruments");
		addCompany("thermo fisher", "TMO", "Thermo Fisher Scientific");
		addCompany("tiffany", "TIF", "Tiffany & Co");
		addCompany("toll brothers", "TOL", "Toll Brothers");
		addCompany("ulta salon", "ULTA", "Ulta Salon, Cosmetics & Fragrance");
		addCompany("vail resorts", "MTN", "Vail Resorts");
		addCompany("veeva", "VEEV", "Veeva Systems");
		addCompany("verint", "VRNT", "Verint Systems");
		addCompany("visa", "V", "Visa");
		addCompany("weibo", "WB", "Weibo");
		addCompany("wix", "WIX", "Wix.com");
		addCompany("work day", "WDAY", "Workday");
		addCompany("right medical", "WMGI", "Wright Medical Group");
		addCompany("yahoo", "YHOO", "Yahoo!");
		addCompany("yelp", "YELP", "Yelp");
		addCompany("zayo", "ZAYO", "Zayo Group Holdings");
		addCompany("zillow", "Z", "Zillow Group");
		
		EntityManager entityManager = JPAHelper.getEntityManager();
		List<Company> companyList = CompanyUtils.fetchAllCompany(entityManager);
		for (Company company : companyList) {
			boolean isSnP500 = company.getIsSnP500() != null && company.getIsSnP500().equals("Y");
			if (isSnP500) {
				if (companyFactsBySymbolMap.get(company.getSymbol()) == null) {
					String nameLower = StringUtils.stripTrailingCompanyTypeFromName(company.getName()).toLowerCase();
					addCompany(nameLower, company.getSymbol(), company.getName());
					System.out.println(nameLower);
				}
			}
		}
	}
	

	private static void addCompany(String name, String symbol, String fullName) {
		CompanyFacts cf = new CompanyFacts(name, symbol, fullName);
		companyFactsByNameMap.put(name, cf);
		companyFactsBySymbolMap.put(symbol, cf);
	}
	
	
	public static CompanyFacts getCompanyFactsForName(String name) {
		if (name == null) {
			return null;
		}
		return companyFactsByNameMap.get(name.toLowerCase());
	}


	public static CompanyFacts getCompanyFactsForSymbol(String symbol) {
		if (symbol == null) {
			return null;
		}
		return companyFactsBySymbolMap.get(symbol.toUpperCase());
	}
	
	
	public static void main(String[] args) {
		CompanyFacts cf = getCompanyFactsForSymbol("AMZN");
		System.out.println(cf.getFullName());
		System.exit(0);
	}
}
