package com.sanjoyghosh.company.db;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.Portfolio;
import com.sanjoyghosh.company.db.model.PortfolioItem;
import com.sanjoyghosh.company.earnings.utils.CompanyFacts;

public class PortfolioJPA {

    private static final Logger logger = Logger.getLogger(PortfolioJPA.class.getPackage().getName());

	public static final String MY_PORTFOLIO_NAME = "MyPortfolio";

	
	public static Portfolio fetchPortfolio(String name, String alexaUserId) {
		EntityManager em = JPAHelper.getEntityManager();
		try {
			Portfolio portfolio = em.createQuery("SELECT p FROM Portfolio p WHERE name = :name AND alexaUserId = :alexaUserId", Portfolio.class)
				.setParameter("name", name)
				.setParameter("alexaUserId", alexaUserId)
				.getSingleResult();
			return portfolio;
		}
		catch (NoResultException e) {}
		return null;
	}

	
	public static Portfolio deletePortfolioItemList(String name, String alexaUserId) {
		EntityManager em = JPAHelper.getEntityManager();
		try {
			em.createQuery("DELETE FROM PortfolioItem WHERE portfolioId IN (SELECT id FROM Portfolio WHERE name = :name AND alexaUserId = :alexaUserId)")
				.setParameter("name", name)
				.setParameter("alexaUserId", alexaUserId)
				.executeUpdate();
		}
		catch (NoResultException e) {}
		return null;
	}
	
	
	public static Portfolio fetchOrCreatePortfolio(String alexaUserId) {
		Portfolio portfolio = PortfolioJPA.fetchPortfolio(MY_PORTFOLIO_NAME, alexaUserId);
		if (portfolio == null) {
			portfolio = new Portfolio();
			portfolio.setName(MY_PORTFOLIO_NAME);
			portfolio.setAlexaUserId(alexaUserId);
			portfolio.setCreateDate(LocalDate.now());
			portfolio.setUpdateDate(LocalDate.now());
			portfolio.setPortfolioItemList(new ArrayList<PortfolioItem>());
		}
		else {
			Map<String, PortfolioItem> portfolioItemBySymbolMap = portfolio.getPortfolioItemBySymbolMap();
			for (PortfolioItem portfolioItem : portfolio.getPortfolioItemList()) {
				portfolioItemBySymbolMap.put(portfolioItem.getCompany().getSymbol(), portfolioItem);
			}
		}
		return portfolio;
	}
	
	
	public static void makePortfolioItem(Portfolio portfolio, String symbol, Double quantity) {
	    Company company = CompanyJPA.fetchCompanyBySymbol(symbol);
	    if (company != null) {
	    	PortfolioItem portfolioItem = portfolio.getPortfolioItemBySymbolMap().get(company.getSymbol());
	    	if (portfolioItem == null) {
	    		portfolioItem = new PortfolioItem();
	    		portfolioItem.setCompany(company);
	    		portfolioItem.setCreateDate(LocalDate.now());
	    		portfolioItem.setPortfolio(portfolio);
	    		
	    		portfolio.getPortfolioItemBySymbolMap().put(company.getSymbol(), portfolioItem);
	    		portfolio.getPortfolioItemList().add(portfolioItem);
	    	}
    		portfolioItem.setQuantity(quantity + portfolioItem.getQuantity());
    		portfolioItem.setValidateDate(LocalDate.now());
	    }
	}
	
	
	@SuppressWarnings("unchecked")
	public static List<CompanyFacts> fetchCompanyFactsForPortfolioWithEarnings(
		String portfolioName, String portfolioAlexaUserId, LocalDate startDate, LocalDate endDate) {
		String sql = 
			"SELECT DISTINCT c.symbol, c.speechName " +
			"FROM Company AS c, Portfolio AS p, PortfolioItem AS pi, EarningsDate AS e " +
			"WHERE " + 
				"p.name = :portfolioName AND p.alexaUserId = :portfolioAlexaUserId " +
				"AND pi.portfolioId = p.id " +
				"AND pi.companyId = c.id " +
				"AND c.id = e.companyId " +
				"AND e.earningsDate >= :startDate AND e.earningsDate <= :endDate " +
			"ORDER BY c.symbol ASC";
		try {
			List<Object[]> list = JPAHelper.getEntityManager().createNativeQuery(sql)
					.setParameter("portfolioName", portfolioName)
					.setParameter("portfolioAlexaUserId", portfolioAlexaUserId)
					.setParameter("startDate", startDate)
					.setParameter("endDate", endDate)
					.getResultList();
			List<CompanyFacts> companyFactsList = new ArrayList<>();
			for (Object[] item : list) {
				CompanyFacts companyFacts = new CompanyFacts(item[0].toString(), item[1].toString());
				companyFactsList.add(companyFacts);
			}
			return companyFactsList;
		}
		catch (NoResultException e) {
		}
		return null;
	}
}
