package com.sanjoyghosh.company.db;

import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;

import com.sanjoyghosh.company.db.model.Portfolio;
import com.sanjoyghosh.company.source.fidelity.FidelityPortfolioReader;
import com.sanjoyghosh.company.source.merrilllynch.MerrillLynchPortfolioReader;

public class PortfolioUpdater {

	public static final String MY_ALEXA_USER_ID = "amzn1.ask.account.AG3AH7ORTENZGSI5ATVRSNF2V4C2QK6CH3IXLPQMPLAWCCTWZNMGGWOGNVG5E6742XCHBILJRV6IIPQHMBLZ6L7TTTZSBVXRDEC567NDTNJHCJBN5P2JXH3C7XEDD7FSHUGIDIOKG7LTDXPZUU7XGF5VXNDMCKUV7CNL7CI7DVAWKANDCHLHWCJDQYS4VITDDBVTOPJ7FSV2MQQ";

	
	public static void main(String[] args) {
		List<String> mySQLHostList = JPAHelper.getMySQLHostList();
		for (String mySQLHost : mySQLHostList) {
			
			EntityManager em = null;
			Portfolio portfolio = null;
			try {
				em = JPAHelper.getEntityManager(mySQLHost);
				em.getTransaction().begin();
				
				PortfolioJPA.deletePortfolioItemList(em, PortfolioJPA.MY_PORTFOLIO_NAME, MY_ALEXA_USER_ID);
				portfolio = PortfolioJPA.fetchOrCreatePortfolio(em, PortfolioUpdater.MY_ALEXA_USER_ID);
				
				FidelityPortfolioReader fidelityReader = new FidelityPortfolioReader(portfolio);
				File[] fidelityFiles = fidelityReader.getFidelityHoldingsFiles();
				for (File fidelityFile : fidelityFiles) {
					try {
						fidelityReader.readFidelityHoldingsFiles(em, fidelityFile);
					}
					catch (Exception e) {
						e.printStackTrace();
						throw e;
					}
				}
				
				MerrillLynchPortfolioReader merrillLynchReader = new MerrillLynchPortfolioReader(portfolio);
				File[] merrillLynchFiles = merrillLynchReader.getMerrillLynchHoldingsFiles();
				for (File merrillLynchFile : merrillLynchFiles) {
					try {
						merrillLynchReader.readMerrillLynchHoldingsFile(em, merrillLynchFile);
					}
					catch (Throwable e) {
						e.printStackTrace();
						throw e;
					}
				}
		
				em.persist(portfolio);
				em.getTransaction().commit();
			}
			catch (Exception e) {
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
			}
			finally {
				if (em != null) {
					em.close();
				}
			}
		}
		
		System.exit(0);
	}
}
