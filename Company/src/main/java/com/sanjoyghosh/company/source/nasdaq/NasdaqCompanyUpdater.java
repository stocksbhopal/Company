package com.sanjoyghosh.company.source.nasdaq;

import java.io.IOException;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EntityManager;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.utils.JsoupUtils;

public class NasdaqCompanyUpdater {

	private static void updateCompany(Company company) throws IOException {
		String url = "http://www.nasdaq.com/symbol/" + company.getSymbol();
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
		System.out.println(td.text());
		
		/*
		Double price = Double.parseDouble(span.text());
		
		span = span.nextElementSibling();
		Double priceChange = Double.parseDouble(span.text());

		span = span.nextElementSibling();
		boolean isUp = span.text().equals("▲");

		span = span.nextElementSibling();
		String percentStr = span.text();
		percentStr = percentStr.substring(0, percentStr.length() - 1);
		Double priceChangePercent = Double.parseDouble(percentStr);
		
		@Column 
		private Long marketCap;
		@Column
		private String marketCapBM;

		nrq.setPrice(price);
		nrq.setPriceChange(isUp ? priceChange : -priceChange);
		nrq.setPriceChangePercent(isUp ? priceChangePercent : -priceChangePercent);
		*/
	}
	
	
	public static void main(String[] args) {
		EntityManager entityManager = null;
		try {
			entityManager = JPAHelper.getEntityManager();
			List<Company> companyList = CompanyUtils.fetchAllCompany(entityManager);
			
//			entityManager.getTransaction().begin();
			for (Company company : companyList) {
				updateCompany(company);
			}
//			entityManager.getTransaction().commit();
		} 
		catch (Exception e) {
			e.printStackTrace();
			if (entityManager != null) {
//				entityManager.getTransaction().rollback();
			}
		}
		
		System.exit(0);
	}
}
