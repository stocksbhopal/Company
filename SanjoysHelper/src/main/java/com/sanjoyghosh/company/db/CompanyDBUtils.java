package com.sanjoyghosh.company.db;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import com.sanjoyghosh.company.api.CompanyEarnings;
import com.sanjoyghosh.company.api.MarketIndexEnum;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.EarningsDate;

public class CompanyDBUtils {

	public static Company fetchCompanyBySymbol(EntityManager entityManager, String symbol) {
		try {
			Company company = 
				entityManager.createQuery("SELECT c FROM Company AS c WHERE c.symbol = :symbol", Company.class)
				.setParameter("symbol", symbol)
				.getSingleResult();
				return company;
		}
		catch (Exception e) {
			System.err.println("No company for: " + symbol);
			e.printStackTrace();
			return null;
		}
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
