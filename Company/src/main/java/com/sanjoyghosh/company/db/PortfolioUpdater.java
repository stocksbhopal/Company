package com.sanjoyghosh.company.db;

import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;

import com.sanjoyghosh.company.db.model.Portfolio;
import com.sanjoyghosh.company.source.fidelity.FidelityPortfolioReader;
import com.sanjoyghosh.company.source.merrilllynch.MerrillLynchPortfolioReader;

public class PortfolioUpdater {
	
	public static void main(String[] args) {
		List<String> mySQLHostList = JPAHelper.getMySQLHostList();
		for (String mySQLHost : mySQLHostList) {
			
			EntityManager em = null;
			Portfolio portfolio = null;
			try {
				em = JPAHelper.getEntityManager(mySQLHost);
				em.getTransaction().begin();
				
				PortfolioJPA.deletePortfolioItemList(em, PortfolioJPA.MY_PORTFOLIO_NAME, PortfolioJPA.MY_ALEXA_USER_ID);
				portfolio = PortfolioJPA.fetchOrCreatePortfolio(em, PortfolioJPA.MY_ALEXA_USER_ID);
				
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
