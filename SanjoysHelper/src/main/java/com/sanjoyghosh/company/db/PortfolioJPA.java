package com.sanjoyghosh.company.db;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.Portfolio;
import com.sanjoyghosh.company.db.model.PortfolioItem;

public class PortfolioJPA {

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
}
