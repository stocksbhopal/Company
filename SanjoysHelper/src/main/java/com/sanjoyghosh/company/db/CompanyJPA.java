package com.sanjoyghosh.company.db;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sanjoyghosh.company.db.model.Company;

public class CompanyJPA {
	
	private static Map<Integer, Company> companyByIdMap;
	private static Map<String, Company> companyBySymbolMap;
	
	
	static {
		companyByIdMap = new HashMap<>();
		companyBySymbolMap = new HashMap<>();
		List<Company> companyList = JPAHelper.getEntityManager().createQuery("SELECT c FROM Company AS c", Company.class).getResultList();
		for (Company company : companyList) {
			companyByIdMap.put(company.getId(), company);
			companyBySymbolMap.put(company.getSymbol().toUpperCase(), company);
		}
	}

	
	public static Collection<Company> fetchCompanies() {
		return companyByIdMap.values();
	}
	
	
	public static Company fetchCompanyBySymbol(String symbol) {
		return companyBySymbolMap.get(symbol.trim().toUpperCase());
	}


	public static Company fetchCompanyById(int id) {
		return companyByIdMap.get(id);
	}
}
