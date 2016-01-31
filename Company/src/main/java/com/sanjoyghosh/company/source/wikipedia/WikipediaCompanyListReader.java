package com.sanjoyghosh.company.source.wikipedia;

import java.io.IOException;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.utils.CompanyUtils;
import com.sanjoyghosh.company.utils.JsoupUtils;

public class WikipediaCompanyListReader {

	private EntityManager entityManager;
	private Map<String, Company> companyBySymbolMap;

	private static final int LIST_SNP500 = 1;
	private static final int LIST_DJIA = 2;
	private static final int LIST_NASDAQ100= 3;
	
	
	private void readListOfCompaniesPage(String url, int listType) throws IOException {
		Document doc = JsoupUtils.fetchDocument(url);
		Elements elements = doc.select("table");
		Element table = elements.get((listType == LIST_SNP500) ? 0 : 1);
		Element tbody = table.select("tbody").get(0);
		Elements trs = tbody.select("tr");
		
		entityManager.getTransaction().begin();
		// Skip the first <tr>.
		for (int i = 1; i < trs.size(); i++) {
			Element td = trs.get(i).select("td").get((listType == LIST_SNP500) ? 0 : 2);
			String symbol = td.text();

			if (!companyBySymbolMap.containsKey(symbol)) {
				System.err.println("NO COMPANY FOR " + symbol);
			}
			else {
				Company company = companyBySymbolMap.get(symbol);
				switch (listType) {
					case LIST_DJIA: company.setIsDJIA("Y"); break;
					case LIST_NASDAQ100: company.setIsNasdaq100("Y"); break;
					case LIST_SNP500: company.setIsSnP500("Y"); break;
				}
				entityManager.persist(company);
			}
		}
		entityManager.getTransaction().commit();
	}
	

	private void readNasdaq100List(String url) throws IOException {
		entityManager.getTransaction().begin();
		Document doc = JsoupUtils.fetchDocument(url);
		
		Elements liList = doc.select("ol").get(0).select("li");
		for (int i = 0; i < liList.size(); i++) {
			Element li = liList.get(i);
			String symbol = li.text().trim();
			int pos = symbol.indexOf('(');
			symbol = symbol.substring(pos + 1, symbol.length() - 1);

			if (!companyBySymbolMap.containsKey(symbol)) {
				System.err.println("NO COMPANY FOR " + symbol);
			}
			else {
				Company company = companyBySymbolMap.get(symbol);
				company.setIsNasdaq100("Y");
				entityManager.persist(company);
			}
		}
		entityManager.getTransaction().commit();
	}

	
	private void updateListOfCompanies() {
		try {
			entityManager = JPAHelper.getEntityManager();
			companyBySymbolMap = CompanyUtils.fetchAllCompanyBySymbolMap(entityManager);
			for (Company company : companyBySymbolMap.values()) {
				company.setIsDJIA("N");
				company.setIsNasdaq100("N");
				company.setIsSnP500("N");
			}

			readListOfCompaniesPage("https://en.wikipedia.org/wiki/Dow_Jones_Industrial_Average", LIST_DJIA);
			readNasdaq100List("https://en.wikipedia.org/wiki/NASDAQ-100");
			readListOfCompaniesPage("https://en.wikipedia.org/wiki/List_of_S%26P_500_companies", LIST_SNP500);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		WikipediaCompanyListReader reader = new WikipediaCompanyListReader();
		reader.updateListOfCompanies();
		System.exit(0);
	}
}
