package com.sanjoyghosh.company.db;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.DividendHistory;
import com.sanjoyghosh.company.db.model.EarningsDate;
import com.sanjoyghosh.company.db.model.Holding;
import com.sanjoyghosh.company.db.model.PriceHistory;
import com.sanjoyghosh.company.db.model.StockSplitHistory;

public class CompanyUtils {

	public static Company fetchCompanyBySymbol(EntityManager entityManager, String symbol) {
		try {
			Company company = 
				entityManager.createQuery("SELECT c FROM Company AS c WHERE c.symbol = :symbol", Company.class)
				.setParameter("symbol", symbol)
				.getSingleResult();
				return company;
		}
		catch (Exception e) {return null;}
	}

	public static List<Company> fetchAllCompany(EntityManager entityManager) {
		List<Company> companyList = 
			entityManager.createQuery("SELECT c FROM Company AS c ORDER BY c.symbol ASC", Company.class)
			.getResultList();
		return companyList;
	}
	
	public static List<String> fetchAllCompanySymbols(EntityManager entityManager) {
		List<String> symbols = 
			entityManager.createQuery("SELECT c.symbol FROM Company AS c ORDER BY c.symbol ASC", String.class)
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
			entityManager.createQuery("SELECT h FROM Holding AS h ORDER BY h.symbol ASC", Holding.class)
			.getResultList();
		return holdingList;
	}
	
	public static List<Holding> fetchAllHoldingAtBrokerage(EntityManager entityManager, String brokerage) {
		List<Holding> holdingList = 
			entityManager.createQuery("SELECT h FROM Holding AS h WHERE h.brokerage = :brokerage ORDER BY h.symbol ASC", Holding.class)
			.setParameter("brokerage", brokerage)
			.getResultList();
		return holdingList;
	}

	public static List<String> fetchAllHoldingSymbols(EntityManager entityManager) {
		List<String> symbols = 
			entityManager.createQuery("SELECT h.symbol FROM Holding AS h ORDER BY h.symbol ASC", String.class)
			.getResultList();
		return symbols;
	}




	public static DividendHistory fetchDividendHistoryForSymbolDate(EntityManager entityManager, String symbol, Timestamp dividendDate) {
		List<DividendHistory> dividendHistoryList = 
			entityManager.createQuery("SELECT dh FROM DividendHistory AS dh WHERE dh.symbol = :symbol AND dh.dateOfDividend = :dividendDate", DividendHistory.class)
			.setParameter("symbol", symbol)
			.setParameter("dividendDate", dividendDate)
			.getResultList();
		return dividendHistoryList == null || dividendHistoryList.size() == 0 ? null : dividendHistoryList.get(0);
	}

	public static List<DividendHistory> fetchDividendHistoryForSymbol(EntityManager entityManager, String symbol) {
		List<DividendHistory> dividendHistoryList = 
			entityManager.createQuery("SELECT dh FROM DividendHistory AS dh WHERE dh.symbol = :symbol", DividendHistory.class)
			.setParameter("symbol", symbol)
			.getResultList();
		return dividendHistoryList;
	}



	
	public static StockSplitHistory fetchStockSplitHistoryForSymbolDate(EntityManager entityManager, String symbol, Timestamp stockSplitDate) {
		List<StockSplitHistory> stockSplitHistoryList = 
			entityManager.createQuery("SELECT sh FROM StockSplitHistory AS sh WHERE sh.symbol = :symbol AND sh.dateOfSplit = :stockSplitDate", StockSplitHistory.class)
			.setParameter("symbol", symbol)
			.setParameter("stockSplitDate", stockSplitDate)
			.getResultList();
		return stockSplitHistoryList == null || stockSplitHistoryList.size() == 0 ? null : stockSplitHistoryList.get(0);
	}

	public static List<StockSplitHistory> fetchStockSplitHistoryForSymbol(EntityManager entityManager, String symbol) {
		List<StockSplitHistory> stockSplitHistoryList = 
			entityManager.createQuery("SELECT sh FROM StockSplitHistory AS sh WHERE sh.symbol = :symbol", StockSplitHistory.class)
			.setParameter("symbol", symbol)
			.getResultList();
		return stockSplitHistoryList;
	}
	


	
	public static PriceHistory fetchLastPriceHistoryForSymbol(EntityManager entityManager, String symbol) {
		List<PriceHistory> priceHistoryList = 
			entityManager.createQuery("SELECT ph FROM PriceHistory AS ph WHERE ph.symbol = :symbol AND ph.dateOfPrice IN (SELECT MAX(dateOfPrice) FROM PriceHistory WHERE symbol = :symbol)", PriceHistory.class)
			.setParameter("symbol", symbol)
			.getResultList();
		return priceHistoryList == null || priceHistoryList.size() == 0 ? null : priceHistoryList.get(0);
	}
	
	public static List<PriceHistory> fetchPriceHistoryForSymbolDateStartEnd(EntityManager entityManager, String symbol, Timestamp priceDateStart, Timestamp priceDateEnd) {
		List<PriceHistory> priceHistoryList = 
			entityManager.createQuery("SELECT ph FROM PriceHistory AS ph WHERE ph.symbol = :symbol AND ph.dateOfPrice >= :priceDateStart AND ph.dateOfPrice <= :priceDateEnd", PriceHistory.class)
			.setParameter("symbol", symbol)
			.setParameter("priceDateStart", priceDateStart)
			.setParameter("priceDateEnd", priceDateEnd)
			.getResultList();
		return priceHistoryList;
	}

	
	

	public static EarningsDate fetchEarningsDateForSymbolDate(EntityManager entityManager, String symbol, Timestamp earningsDate) {
		List<EarningsDate> earningsDateList = 
			entityManager.createQuery("SELECT ed FROM EarningsDate AS ed WHERE ed.symbol = :symbol AND ed.earningsDate = :earningsDate", EarningsDate.class)
			.setParameter("symbol", symbol)
			.setParameter("earningsDate", earningsDate)
			.getResultList();
		return earningsDateList == null || earningsDateList.size() == 0 ? null : earningsDateList.get(0);
	}

	public static EarningsDate fetchLastEarningsDateForSymbol(EntityManager entityManager, String symbol) {
		List<EarningsDate> earningsDateList = 
			entityManager.createQuery("SELECT ed FROM EarningsDate AS ed WHERE ed.symbol = :symbol ORDER BY ed.earningsDate DESC", EarningsDate.class)
			.setParameter("symbol", symbol)
			.setMaxResults(1)
			.getResultList();
		return earningsDateList == null || earningsDateList.size() == 0 ? null : earningsDateList.get(0);
	}
}
