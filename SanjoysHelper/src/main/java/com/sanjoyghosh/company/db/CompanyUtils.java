package com.sanjoyghosh.company.db;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import com.sanjoyghosh.company.api.CompanyEarnings;
import com.sanjoyghosh.company.api.MarketIndexEnum;
import com.sanjoyghosh.company.db.model.Activity;
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

	public static Map<Integer, Company> fetchAllCompanyByIdMap(EntityManager entityManager) {
		List<Company> companyList = 
			entityManager.createQuery("SELECT c FROM Company AS c", Company.class)
			.getResultList();
		Map<Integer, Company> companyByIdMap = new HashMap<Integer, Company>();
		for (Company company : companyList) {
			companyByIdMap.put(company.getId(), company);
		}
		return companyByIdMap;
	}

	
	
	
	/**
	 * For Merrill Lynch, settledDate is Non Null.
	 */
	public static Set<Activity> fetchAllActivityAtBrokerageForSettledDate(EntityManager entityManager, String brokerage, LocalDate settledDate) {
		List<Activity> activityList = 
			entityManager.createQuery("SELECT a FROM Activity AS a WHERE a.brokerage = :brokerage AND a.settledDate = :settledDate", Activity.class)
			.setParameter("brokerage", brokerage)
			.setParameter("settledDate", settledDate)
			.getResultList();
		Set<Activity> activitySet = new HashSet<Activity>();
		activitySet.addAll(activityList);
		return activitySet;
	}

	/**
	 * For Fidelity, tradeDate is Non Null.
	 */
	public static Set<Activity> fetchAllActivityAtBrokerageForTradeDate(EntityManager entityManager, String brokerage, Timestamp tradeDate) {
		List<Activity> activityList = 
			entityManager.createQuery("SELECT a FROM Activity AS a WHERE a.brokerage = :brokerage AND a.tradeDate = :tradeDate", Activity.class)
			.setParameter("brokerage", brokerage)
			.setParameter("tradeDate", tradeDate)
			.getResultList();
		Set<Activity> activitySet = new HashSet<Activity>();
		activitySet.addAll(activityList);
		return activitySet;
	}
	
	
	
	public static List<Holding> fetchAllHoldingsAtBrokerage(EntityManager entityManager, String brokerage) {
		List<Holding> holdingList = 
			entityManager.createQuery("SELECT h FROM Holding AS h WHERE h.brokerage = :brokerage ORDER BY h.symbol ASC", Holding.class)
			.setParameter("brokerage", brokerage)
			.getResultList();
		return holdingList;
	}

	public static List<String> fetchAllHoldingsSymbols(EntityManager entityManager) {
		List<String> symbols = 
			entityManager.createQuery("SELECT h.symbol FROM Holding AS h ORDER BY h.symbol ASC", String.class)
			.getResultList();
		return symbols;
	}

	public static int deleteAllHoldingsByDateBrokerage(EntityManager entityManager, String brokerage, Date cobDate) {
		int numHoldingsDeleted = 
			entityManager.createQuery("DELETE FROM Holding AS h WHERE h.brokerage = :brokerage AND h.cobDate = :cobDate")
			.setParameter("brokerage", brokerage)
			.setParameter("cobDate", cobDate)
			.executeUpdate();
		return numHoldingsDeleted;
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
			entityManager.createQuery("SELECT dh FROM DividendHistory AS dh WHERE dh.symbol = :symbol ORDER BY dh.dateOfDividend DESC", DividendHistory.class)
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
			entityManager.createQuery("SELECT sh FROM StockSplitHistory AS sh WHERE sh.symbol = :symbol ORDER BY sh.dateOfSplit DESC", StockSplitHistory.class)
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

	
	/*
	public static List<CompanyPrice> fetchCompanyPriceListForAlexaUserLimit(EntityManager entityManager, String alexaUser, int limit, boolean gainers) {
		List<CompanyPrice> companyPriceList = entityManager.createQuery(
			"SELECT new com.sanjoyghosh.company.api.CompanyPrice(c.symbol, c.name, p.price, p.priceChange, p.priceChangePercent) " +
				"FROM MyStocks AS m, Company AS c, Price AS p, AlexaUser AS a " +
				"WHERE a.alexaUser = :alexaUser AND a.id = m.alexaUserId AND m.companyId = c.id AND c.id = p.companyId " +
				"ORDER BY p.priceChangePercent " + (gainers ? "DESC" : "ASC"), CompanyPrice.class)
				.setParameter("alexaUser", alexaUser)
				.setMaxResults(limit)
				.getResultList();
		return companyPriceList;
	}

	public static List<CompanyPrice> fetchCompanyPriceListForAlexaUser(EntityManager entityManager, String alexaUser) {
		List<CompanyPrice> companyPriceList = entityManager.createQuery(
			"SELECT new com.sanjoyghosh.company.api.CompanyPrice(c.symbol, c.name, p.price, p.priceChange, p.priceChangePercent) " +
				"FROM MyStocks AS m, Company AS c, Price AS p, AlexaUser AS a " +
				"WHERE a.alexaUser = :alexaUser AND a.id = m.alexaUserId AND m.companyId = c.id AND c.id = p.companyId " +
				"ORDER BY p.priceChangePercent DESC", CompanyPrice.class)
				.setParameter("alexaUser", alexaUser)
				.getResultList();
		return companyPriceList;
	}
	
	
	public static List<Price> fetchPriceList(EntityManager entityManager) {
		List<Price> priceList = entityManager.createQuery("SELECT p FROM Price AS p", Price.class).getResultList();
		return priceList;
	}
	
	
	
	public static List<Company> fetchCompanyListForAllUsersMinusPrice(EntityManager entityManager) {
		List<Company> companyList =
			entityManager.createQuery(
				"SELECT new com.sanjoyghosh.company.db.model.Company(c.id, c.symbol) FROM MyStocks AS m, Company AS c WHERE m.companyId = c.id AND " +
					"m.companyId IN (SELECT DISTINCT(companyId) FROM MyStocks) AND " +
					"m.companyId NOT IN (SELECT companyId FROM Price)", Company.class)
			.getResultList();
		return companyList;
	}

	public static List<Company> fetchCompanyListForAlexaUserId(EntityManager entityManager, int alexaUserId) {
		List<Company> companyList =
			entityManager.createQuery("SELECT new com.sanjoyghosh.company.db.model.Company(c.id, c.symbol) FROM MyStocks AS m, Company AS c WHERE m.companyId = c.id AND alexaUserId = :alexaUserId", Company.class)
			.setParameter("alexaUserId", alexaUserId)
			.getResultList();
		return companyList;
	}
	
	
	public static List<Portfolio> fetchMyStocksListForAlexaUser(EntityManager entityManager, String alexaUser) {
		List<Portfolio> myStocksList =
			entityManager.createQuery("SELECT m FROM MyStocks AS m, AlexaUser AS a WHERE m.alexaUserId = a.id AND a.alexaUser = :alexaUser", Portfolio.class)
			.setParameter("alexaUser", alexaUser)
			.getResultList();
		return myStocksList;		
	}
	
	public static Map<Integer, Portfolio> fetchMyStocksMapByCompanyIdForAlexaUser(EntityManager entityManager, String alexaUser) {
		List<Portfolio> myStocksList =
			entityManager.createQuery("SELECT m FROM MyStocks AS m, AlexaUser AS a WHERE m.alexaUserId = a.id AND a.alexaUser = :alexaUser", Portfolio.class)
			.setParameter("alexaUser", alexaUser)
			.getResultList();
		
		Map<Integer, Portfolio> myStocksByCompanyIdMap = new HashMap<>();
		for (Portfolio myStocks : myStocksList) {
			myStocksByCompanyIdMap.put(myStocks.getCompanyId(), myStocks);
		}
		return myStocksByCompanyIdMap;		
	}
	*/
	
	
	private static String marketIndexToColumn(MarketIndexEnum index) {
		switch (index.getIndex()) {
		case MarketIndexEnum.INDEX_NONE: return "";
		case MarketIndexEnum.INDEX_DJIA: return "isDJIA";
		case MarketIndexEnum.INDEX_NASDAQ100: return "isNasdaq100";
		case MarketIndexEnum.INDEX_SNP500: return "isSnP500";
		default: return "";
		}
	}

	public static List<CompanyEarnings> fetchEarningsDateListForDateRange(EntityManager entityManager, Timestamp earningsDateStart, Timestamp earningsDateEnd) {
		List<CompanyEarnings> earningsDateList = 
			entityManager.createQuery("SELECT new com.sanjoyghosh.company.api.CompanyEarnings(ed.symbol, c.name, ed.earningsDate, ed.beforeMarketOrAfterMarket) " +
				"FROM EarningsDate AS ed, Company AS c " +
				"WHERE ed.earningsDate >= :earningsDateStart AND ed.earningsDate <= :earningsDateEnd AND ed.companyId = c.id " +
				"ORDER BY ed.earningsDate ASC, ed.beforeMarketOrAfterMarket DESC", CompanyEarnings.class)
			.setParameter("earningsDateStart", earningsDateStart)
			.setParameter("earningsDateEnd", earningsDateEnd)
			.getResultList();
		return earningsDateList;
	}

	public static List<CompanyEarnings> fetchEarningsDateListForDateRangeAndAlexaUser(EntityManager entityManager, LocalDate earningsDateStart, LocalDate earningsDateEnd, String alexaUser) {
		List<CompanyEarnings> earningsDateList = 
			entityManager.createQuery("SELECT new com.sanjoyghosh.company.api.CompanyEarnings(e.symbol, c.name, e.earningsDate, e.beforeMarketOrAfterMarket) " +
				"FROM EarningsDate AS e, Company AS c " +
				"WHERE e.earningsDate >= :earningsDateStart AND e.earningsDate <= :earningsDateEnd AND e.companyId = c.id AND e.symbol IN " +
		           "(SELECT co.symbol FROM Company AS co, MyStocks AS ms, AlexaUser AS au WHERE au.alexaUser = :alexaUser AND au.id = ms.alexaUserId AND ms.companyId = co.id)" + 
				"ORDER BY e.earningsDate ASC, e.beforeMarketOrAfterMarket DESC", CompanyEarnings.class)
			.setParameter("earningsDateStart", earningsDateStart)
			.setParameter("earningsDateEnd", earningsDateEnd)
			.setParameter("alexaUser", alexaUser)
			.getResultList();
		return earningsDateList;
	}

	public static List<CompanyEarnings> fetchEarningsDateListForMarketIndexNext(EntityManager entityManager, LocalDate earningsDateStart, MarketIndexEnum marketIndex) {
		List<CompanyEarnings> earningsDateList = 
			entityManager.createQuery(
				"SELECT new com.sanjoyghosh.company.api.CompanyEarnings(e.symbol, c.name, e.earningsDate, e.beforeMarketOrAfterMarket) " +
				"FROM EarningsDate AS e, Company AS c " +
				"WHERE e.companyId = c.id AND c." + marketIndexToColumn(marketIndex) + " = 'Y' AND e.earningsDate IN " +
					"(SELECT MIN(ed.earningsDate) FROM EarningsDate AS ed, Company AS co WHERE ed.companyId = co.id AND ed.earningsDate >= :earningsDateStart AND co." + marketIndexToColumn(marketIndex) + " = 'Y') " +
				"ORDER BY e.beforeMarketOrAfterMarket DESC", CompanyEarnings.class)
			.setParameter("earningsDateStart", earningsDateStart)
			.getResultList();
		return earningsDateList;
	}

	public static List<CompanyEarnings> fetchEarningsDateListForNextAndAlexaUser(EntityManager entityManager, LocalDate earningsDateStart, String alexaUser) {
		List<CompanyEarnings> earningsDateList = 
			entityManager.createQuery(
				"SELECT new com.sanjoyghosh.company.api.CompanyEarnings(e.symbol, c.name, e.earningsDate, e.beforeMarketOrAfterMarket) " +
				"FROM EarningsDate AS e, Company AS c " +
				"WHERE " + 
				    "e.companyId = c.id AND " +
				    "e.symbol IN (SELECT co.symbol FROM Company AS co, MyStocks AS ms, AlexaUser AS au WHERE au.alexaUser = :alexaUser AND au.id = ms.alexaUserId AND ms.companyId = co.id) AND " + 
				    "e.earningsDate IN " +
					   "(SELECT MIN(ed.earningsDate) FROM EarningsDate AS ed WHERE ed.earningsDate >= :earningsDateStart AND ed.symbol IN " + 
				          "(SELECT co.symbol FROM Company AS co, MyStocks AS ms, AlexaUser AS au WHERE au.alexaUser = :alexaUser AND au.id = ms.alexaUserId AND ms.companyId = co.id)" + 
					   ") " +
				"ORDER BY e.beforeMarketOrAfterMarket DESC", CompanyEarnings.class)
			.setParameter("earningsDateStart", earningsDateStart)
			.setParameter("alexaUser", alexaUser)
			.getResultList();
		return earningsDateList;
	}

	public static List<EarningsDate> fetchEarningsDateListForSymbolDate(EntityManager entityManager, String symbol, LocalDate earningsDate) {
		List<EarningsDate> earningsDateList = 
			entityManager.createQuery("SELECT ed FROM EarningsDate AS ed WHERE ed.symbol = :symbol AND ed.earningsDate >= :earningsDate", EarningsDate.class)
			.setParameter("symbol", symbol)
			.setParameter("earningsDate", earningsDate)
			.getResultList();
		return earningsDateList;
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
