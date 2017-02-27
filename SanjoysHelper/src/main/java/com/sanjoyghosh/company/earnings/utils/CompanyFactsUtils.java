package com.sanjoyghosh.company.earnings.utils;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;

import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;

public class CompanyFactsUtils {

	private static final HashMap<String, CompanyFacts> companyFactsByNameMap = new HashMap<>();
	private static final HashMap<String, CompanyFacts> companyFactsBySymbolMap = new HashMap<>();
	static {
		loadCompanyNames();
	}
	
	
	private static void loadCompanyNames() {	
		int duplicateCount = 0;
		EntityManager entityManager = JPAHelper.getEntityManager();
		List<Company> companyList = CompanyUtils.fetchAllCompany(entityManager);
		for (Company company : companyList) {
			CompanyFacts cf = new CompanyFacts(company.getSymbol(), company.getSpeechName());
			companyFactsBySymbolMap.put(company.getSymbol(), cf);
			
			String nameLower = company.getSpeechName().toLowerCase();
			String[] pieces = nameLower.split(" ");
			for (int i = 0; i < pieces.length; i++) {
				// Mostly to strip .com from Amazon.com.
				pieces[i] = (pieces[i].endsWith(".com")) ? pieces[i].substring(0, pieces[i].length() - ".com".length()) : pieces[i];
				pieces[i] = (pieces[i].endsWith(",")) ? pieces[i].substring(0, pieces[i].length() - ",".length()) : pieces[i];
				String partialName = "";
				for (int j = 0; j < (i + 1); j++) {
					partialName += pieces[j] + " ";
				}
				partialName = partialName.trim();
				if (companyFactsByNameMap.get(partialName) != null) {
					String symbol = company.getSymbol().toLowerCase();
					String inNameMapSymbol = companyFactsByNameMap.get(partialName).getSymbol().toLowerCase();
					if (inNameMapSymbol.startsWith(symbol)) {
						companyFactsByNameMap.put(partialName, cf);
					}
					else if (!symbol.startsWith(inNameMapSymbol)) {
						duplicateCount++;
						System.out.println("ERROR: Duplicate partial company name: " + partialName + " FOR: " + 
							companyFactsByNameMap.get(partialName) + " AND: " + cf);
					}
				}
				else {
					companyFactsByNameMap.put(partialName, cf);
				}
			}
		}
		
		// Vornado, VNO
		// Hess, HES
		// Altria Group, MO
		// CalAtlantic Group, CAA
		// Delta Air, DAL.  Processes as da
		// Allergan, AGN
		// Regeneron, REGN
		// Acadia, ACAD
		// PACCAR Inc, PCAR
		// Norfolk Southern, NSC
		// Eaton, ETN
		// AECOM, ACM
		// Crown Holdings, CCK
		companyFactsByNameMap.put("haliburton", companyFactsBySymbolMap.get("HAL"));
		companyFactsByNameMap.put("service now", companyFactsBySymbolMap.get("NOW"));
		companyFactsByNameMap.put("us steel", companyFactsBySymbolMap.get("X"));
		companyFactsByNameMap.put("under armor", companyFactsBySymbolMap.get("UA"));
		companyFactsByNameMap.put("children's", companyFactsBySymbolMap.get("PLCE"));
		companyFactsByNameMap.put("bank united", companyFactsBySymbolMap.get("BKU"));
		companyFactsByNameMap.put("enterprise lp", companyFactsBySymbolMap.get("EPD"));
		companyFactsByNameMap.put("sales force", companyFactsBySymbolMap.get("CRM"));
		companyFactsByNameMap.put("checkpoint", companyFactsBySymbolMap.get("CHKP"));
		companyFactsByNameMap.put("price line", companyFactsBySymbolMap.get("PCLN"));
		companyFactsByNameMap.put("go daddy", companyFactsBySymbolMap.get("GDDY"));
		companyFactsByNameMap.put("gold daddy", companyFactsBySymbolMap.get("GDDY"));
		companyFactsByNameMap.put("synopsis", companyFactsBySymbolMap.get("SNPS"));
		companyFactsByNameMap.put("AT and T", companyFactsBySymbolMap.get("T"));
		companyFactsByNameMap.put("tea", companyFactsBySymbolMap.get("T"));
		companyFactsByNameMap.put("footlocker", companyFactsBySymbolMap.get("FL"));
		
		System.out.println("SIZE: " + companyFactsByNameMap.size() + "   " + duplicateCount);
	}
	
	
	public static void main(String[] args) {
		loadCompanyNames();
		System.exit(0);
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
}
