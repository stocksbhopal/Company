package com.sanjoyghosh.company.db;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import com.sanjoyghosh.company.model.Company;
import com.sanjoyghosh.company.model.EarningsDate;
import com.sanjoyghosh.company.model.Holding;
import com.sanjoyghosh.company.model.PriceHistory;

public class CompanyUtils {

	public static List<Company> fetchAllCompany(EntityManager entityManager) {
		List<Company> companyList = 
			entityManager.createQuery("SELECT c FROM Company AS c", Company.class)
			.getResultList();
		return companyList;
	}
	
	
	public static List<String> fetchAllCompanySymbols(EntityManager entityManager) {
		List<String> symbols = 
			entityManager.createQuery("SELECT c.symbol FROM Company AS c", String.class)
			.getResultList();
		return symbols;
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

	
	public static List<Holding> fetchAllHolding(EntityManager entityManager) {
		List<Holding> holdingList = 
			entityManager.createQuery("SELECT h FROM Holding AS h", Holding.class)
			.getResultList();
		return holdingList;
	}

	
	public static List<Holding> fetchAllHoldingAtBrokerage(EntityManager entityManager, String brokerage) {
		List<Holding> holdingList = 
			entityManager.createQuery("SELECT h FROM Holding AS h WHERE h.brokerage = :brokerage", Holding.class)
			.setParameter("brokerage", brokerage)
			.getResultList();
		return holdingList;
	}


	public static List<String> fetchAllHoldingSymbols(EntityManager entityManager) {
		List<String> symbols = 
			entityManager.createQuery("SELECT h.symbol FROM Holding AS h", String.class)
			.getResultList();
		return symbols;
	}


	public static PriceHistory fetchLastPriceHistoryForSymbol(EntityManager entityManager, String symbol) {
		List<PriceHistory> priceHistoryList = 
			entityManager.createQuery("SELECT ph FROM PriceHistory AS ph WHERE ph.symbol = :symbol AND ph.dateOfPrice IN (SELECT MAX(dateOfPrice) FROM PriceHistory WHERE symbol = :symbol)", PriceHistory.class)
			.setParameter("symbol", symbol)
			.getResultList();
		return priceHistoryList == null || priceHistoryList.size() == 0 ? null : priceHistoryList.get(0);
	}


	public static EarningsDate fetchEarningsDateForSymbolDate(EntityManager entityManager, String symbol, Timestamp earningsDate) {
		List<EarningsDate> earningsDateList = 
			entityManager.createQuery("SELECT ed FROM EarningsDate AS ed WHERE ed.symbol = :symbol AND ed.earningsDate = :earningsDate", EarningsDate.class)
			.setParameter("symbol", symbol)
			.setParameter("earningsDate", earningsDate)
			.getResultList();
		return earningsDateList == null || earningsDateList.size() == 0 ? null : earningsDateList.get(0);
	}
}
