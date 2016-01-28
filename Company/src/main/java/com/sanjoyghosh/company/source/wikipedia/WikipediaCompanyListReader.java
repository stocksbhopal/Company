package com.sanjoyghosh.company.source.wikipedia;

import java.io.IOException;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.JsoupUtils;
import com.sanjoyghosh.company.db.model.Company;

public class WikipediaCompanyListReader {

	private EntityManager entityManager;
	private Map<String, Company> companyBySymbolMap;

	
	private void readListOfCompaniesPage(String url) throws IOException {
		Document doc = JsoupUtils.fetchDocument(url);
		Elements elements = doc.select("table");
		Element table = elements.get(0);
		Element tbody = table.select("tbody").get(0);
		Elements trs = tbody.select("tr");
		
		// Skip the first <tr>.
		for (int i = 1; i < trs.size(); i++) {
			Element td = trs.get(i).select("td").get(0);
			String symbol = td.text();
			if (!companyBySymbolMap.containsKey(symbol)) {
				System.err.println("NO COMPANY FOR " + symbol);
			}
//			System.out.println(td.text());
		}
	}
	
	
	private void updateListOfCompanies(String url) {
		try {
			entityManager = JPAHelper.getEntityManager();
			companyBySymbolMap = CompanyUtils.fetchAllCompanyBySymbolMap(entityManager);
			readListOfCompaniesPage(url);
		}
		catch (IOException e) {
			
		}
	}
	
	
	public static void main(String[] args) {
		WikipediaCompanyListReader reader = new WikipediaCompanyListReader();
		reader.updateListOfCompanies("https://en.wikipedia.org/wiki/List_of_S%26P_500_companies");
		System.exit(0);
	}
}
