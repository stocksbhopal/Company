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
		EntityManager entityManager = JPAHelper.getEntityManager();
		List<Company> companyList = CompanyUtils.fetchAllCompany(entityManager);
		for (Company company : companyList) {
			CompanyFacts cf = new CompanyFacts(company.getSymbol(), company.getName());
			companyFactsBySymbolMap.put(company.getSymbol(), cf);
			
			String nameLower = StringUtils.stripTrailingCompanyTypeFromName(company.getName()).toLowerCase();
			String[] pieces = nameLower.split(" ");
			for (int i = 0; i < pieces.length; i++) {
				// Mostly to strip .com from Amazon.com.
				pieces[i] = (pieces[i].endsWith(".com")) ? pieces[i].substring(0, pieces[i].length() - ".com".length()) : pieces[i];
				String partialName = "";
				for (int j = 0; j < (i + 1); j++) {
					partialName += pieces[j] + " ";
				}
				partialName = partialName.trim();
				if (companyFactsByNameMap.get(partialName) != null) {
					System.out.println("ERROR: Duplicate partial company name: " + partialName + " FOR: " + 
						companyFactsByNameMap.get(partialName).getFullName() + " AND: " + cf.getFullName());
				}
				else {
					companyFactsByNameMap.put(partialName, cf);
					System.out.println(partialName);
				}
			}
		}
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
