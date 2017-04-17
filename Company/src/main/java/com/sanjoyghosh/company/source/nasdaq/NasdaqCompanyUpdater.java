package com.sanjoyghosh.company.source.nasdaq;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.utils.JsoupUtils;
import com.sanjoyghosh.company.utils.StringUtils;

public class NasdaqCompanyUpdater {

	public static void updateCompany(Company company) throws IOException {
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
		Long marketCap = StringUtils.parseIntegerDollarAmount(MarketCapStr);
		if (marketCap != null) {
			company.setMarketCap(marketCap);
		}
		else {
			System.err.println("MarketCap null for: " + symbol);
		}
		
		Elements elements = doc.select("a[href=http://www.nasdaq.com/investing/glossary/#MeanRec]");
		if (elements.size() > 0) {
			Element element = elements.get(0).parent().nextElementSibling();
			String text = element.text().trim();
			try {
				if (text.length() > 0) {
					company.setAnalystOpinion(Double.parseDouble(text));
				}
			}
			catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
	}
}
