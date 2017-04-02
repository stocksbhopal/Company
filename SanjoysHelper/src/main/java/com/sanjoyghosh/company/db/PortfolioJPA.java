package com.sanjoyghosh.company.db;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.Portfolio;
import com.sanjoyghosh.company.db.model.PortfolioItem;

public class PortfolioJPA {

//    private static final Logger logger = Logger.getLogger(PortfolioJPA.class.getName());

	public static final String MY_PORTFOLIO_NAME = "MyPortfolio";

	
	public static Portfolio fetchPortfolio(EntityManager em, String name, String alexaUserId) {
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

	
	public static Portfolio deletePortfolioItemList(EntityManager em, String name, String alexaUserId) {
		try {
			em.createQuery("DELETE FROM PortfolioItem WHERE portfolioId IN (SELECT id FROM Portfolio WHERE name = :name AND alexaUserId = :alexaUserId)")
				.setParameter("name", name)
				.setParameter("alexaUserId", alexaUserId)
				.executeUpdate();
		}
		catch (NoResultException e) {}
		return null;
	}
	
	
	public static Portfolio fetchOrCreatePortfolio(EntityManager em, String alexaUserId) {
		Portfolio portfolio = PortfolioJPA.fetchPortfolio(em, MY_PORTFOLIO_NAME, alexaUserId);
		if (portfolio == null) {
			portfolio = new Portfolio();
			portfolio.setName(MY_PORTFOLIO_NAME);
			portfolio.setAlexaUserId(alexaUserId);
			portfolio.setCreateDate(LocalDate.now());
			portfolio.setUpdateDate(LocalDate.now());
			portfolio.setPortfolioItemList(new ArrayList<PortfolioItem>());
		}
		else {
			portfolio.restorePortfolioItemBySymbolMap();
		}
		return portfolio;
	}
	
	
	public static void makePortfolioItem(EntityManager em, Portfolio portfolio, String symbol, Double quantity) {
	    Company company = CompanyJPA.fetchCompanyBySymbol(em, symbol);
	    if (company != null) {
	    	PortfolioItem portfolioItem = portfolio.getPortfolioItemBySymbol(company.getSymbol());
	    	if (portfolioItem == null) {
	    		portfolioItem = new PortfolioItem();
	    		portfolioItem.setCompany(company);
	    		portfolioItem.setCreateDate(LocalDate.now());
	    		portfolioItem.setPortfolio(portfolio);
	    		
	    		portfolio.addPortfolioItem(portfolioItem);
	    	}
    		portfolioItem.setQuantity(quantity + portfolioItem.getQuantity());
    		portfolioItem.setValidateDate(LocalDate.now());
	    }
	}
	
	public static final String MY_ALEXA_USER_ID = "amzn1.ask.account.AG3AH7ORTENZGSI5ATVRSNF2V4C2QK6CH3IXLPQMPLAWCCTWZNMGGWOGNVG5E6742XCHBILJRV6IIPQHMBLZ6L7TTTZSBVXRDEC567NDTNJHCJBN5P2JXH3C7XEDD7FSHUGIDIOKG7LTDXPZUU7XGF5VXNDMCKUV7CNL7CI7DVAWKANDCHLHWCJDQYS4VITDDBVTOPJ7FSV2MQQ";

	@SuppressWarnings("unchecked")
	public static List<PortfolioItemData> fetchPortfolioItemDataWithEarnings(
		EntityManager em, String portfolioName, String portfolioAlexaUserId, LocalDate startDate, LocalDate endDate) {
		
		String sql = 
			"SELECT DISTINCT c.symbol, c.speechName, pi.quantity " +
			"FROM Company AS c, Portfolio AS p, PortfolioItem AS pi, EarningsDate AS e " +
			"WHERE " + 
				"p.name = :portfolioName AND p.alexaUserId = :portfolioAlexaUserId " +
				"AND pi.portfolioId = p.id " +
				"AND pi.companyId = c.id " +
				"AND c.id = e.companyId " +
				"AND e.earningsDate >= :startDate AND e.earningsDate <= :endDate " +
			"ORDER BY c.speechName ASC";
		try {
			List<Object[]> list = em.createNativeQuery(sql)
					.setParameter("portfolioName", portfolioName)
					.setParameter("portfolioAlexaUserId", portfolioAlexaUserId)
					.setParameter("startDate", startDate)
					.setParameter("endDate", endDate)
					.getResultList();
			List<PortfolioItemData> portfolioItemDataList = new ArrayList<>();
			for (Object[] item : list) {
				PortfolioItemData portfolioItemData = new PortfolioItemData(item[0].toString(), item[1].toString(), (Double)item[2]);
				portfolioItemDataList.add(portfolioItemData);
			}
			return portfolioItemDataList;
		}
		catch (NoResultException e) {
		}
		return null;
	}
}
