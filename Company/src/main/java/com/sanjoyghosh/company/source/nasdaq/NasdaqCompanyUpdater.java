package com.sanjoyghosh.company.source.nasdaq;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.utils.JsoupUtils;
import com.sanjoyghosh.company.utils.StringUtils;

public class NasdaqCompanyUpdater {

	private static void updateCompany(Company company) throws IOException {
		String symbol = company.getSymbol();
		String url = "http://www.nasdaq.com/symbol/" + symbol;
		Document doc = JsoupUtils.fetchDocument(url);
		
		Elements aList = doc.select("a[id=share_outstanding]");
		if (aList == null || 
			aList.first() == null || 
			aList.first().parent() == null || 
			aList.first().parent().nextElementSibling() == null) {
			System.err.println("No proper element chain for url: " + url);
			return;
		}
		
		Element td = aList.first().parent().nextElementSibling();
		String MarketCapStr = td.text();
		System.out.println(symbol + ": " + MarketCapStr);
		
		Long marketCap = StringUtils.parseIntegerDollarAmount(MarketCapStr);
		if (marketCap != null) {
			company.setMarketCap(marketCap);
		}
		else {
			System.err.println("MarketCap null for: " + symbol);
		}
	}
	
	
	public static void main(String[] args) {
		EntityManager entityManager = null;
		try {
			entityManager = JPAHelper.getEntityManager();
			List<Company> companyList = CompanyUtils.fetchAllCompany(entityManager);
			
			entityManager.getTransaction().begin();
			for (Company company : companyList) {
				updateCompany(company);
			}
			entityManager.getTransaction().commit();
		} 
		catch (Exception e) {
			e.printStackTrace();
			if (entityManager != null) {
				entityManager.getTransaction().rollback();
			}
		}
		
		System.exit(0);
	}
}
