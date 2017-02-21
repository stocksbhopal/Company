package com.sanjoyghosh.company.db;

import java.io.File;

import com.sanjoyghosh.company.db.model.Portfolio;
import com.sanjoyghosh.company.source.fidelity.FidelityPortfolioReader;
import com.sanjoyghosh.company.source.merrilllynch.MerrillLynchPortfolioReader;

public class PortfolioUpdater {

	public static final String MY_ALEXA_USER_ID = "amzn1.ask.account.AG3AH7ORTENZGSI5ATVRSNF2V4C2QK6CH3IXLPQMPLAWCCTWZNMGGWOGNVG5E6742XCHBILJRV6IIPQHMBLZ6L7TTTZSBVXRDEC567NDTNJHCJBN5P2JXH3C7XEDD7FSHUGIDIOKG7LTDXPZUU7XGF5VXNDMCKUV7CNL7CI7DVAWKANDCHLHWCJDQYS4VITDDBVTOPJ7FSV2MQQ";

	
	public static void main(String[] args) {
		JPAHelper.getEntityManager().getTransaction().begin();
		PortfolioJPA.deletePortfolioItemList(PortfolioJPA.MY_PORTFOLIO_NAME, MY_ALEXA_USER_ID);
		Portfolio portfolio = PortfolioJPA.fetchOrCreatePortfolio(PortfolioUpdater.MY_ALEXA_USER_ID);
		
		FidelityPortfolioReader fidelityReader = new FidelityPortfolioReader(portfolio);
		File[] fidelityFiles = fidelityReader.getFidelityHoldingsFiles();
		for (File fidelityFile : fidelityFiles) {
			try {
				fidelityReader.readFidelityHoldingsFiles(fidelityFile);
				fidelityFile.delete();
			}
			catch (Throwable e) {
				e.printStackTrace();
				throw e;
			}
		}
		
		MerrillLynchPortfolioReader merrillLynchReader = new MerrillLynchPortfolioReader(portfolio);
		File[] merrillLynchFiles = merrillLynchReader.getMerrillLynchHoldingsFiles();
		for (File merrillLynchFile : merrillLynchFiles) {
			try {
				merrillLynchReader.readMerrillLynchHoldingsFile(merrillLynchFile);
				merrillLynchFile.delete();
			}
			catch (Throwable e) {
				e.printStackTrace();
				throw e;
			}
		}

		JPAHelper.getEntityManager().persist(portfolio);
		JPAHelper.getEntityManager().getTransaction().commit();
		System.exit(0);
	}
}
