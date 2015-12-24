package com.sanjoyghosh.company.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import com.sanjoyghosh.company.model.Company;
import com.sanjoyghosh.company.model.Holding;

public class CompanyUtils {

	public static List<Company> fetchAllCompany(EntityManager entityManager) {
		List<Company> companyList = 
			entityManager.createQuery("SELECT c FROM Company AS c", Company.class)
			.getResultList();
		return companyList;
	}
	

	public static Map<String, Company> fetchAllCompanyBySymbolMap(EntityManager entityManager) {
		List<Company> companyList = 
			entityManager.createQuery("SELECT c FROM Company AS c", Company.class)
			.getResultList();
		Map<String, Company> companyBySymbolMap = new HashMap<String, Company>();
		for (Company company : companyList) {
			companyBySymbolMap.put(company.getSymbol().toUpperCase(), company);
		}
		return companyBySymbolMap;
	}

	
	public static List<Holding> fetchAllHoldingAtBrokerage(EntityManager entityManager, String brokerage) {
		List<Holding> holdingList = 
			entityManager.createQuery("SELECT h FROM Holding AS h WHERE h.brokerage = :brokerage", Holding.class)
			.setParameter("brokerage", brokerage)
			.getResultList();
		return holdingList;
	}
}
