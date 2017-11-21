package com.sanjoyghosh.company.alexaskill.source.reuters;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sanjoyghosh.company.dynamodb.model.Company;
import com.sanjoyghosh.company.utils.JsoupUtils;
import com.sanjoyghosh.company.utils.LocalDateUtils;
import com.sanjoyghosh.company.utils.Utils;

public class ReutersCompanyNewsReader {

	private static ReutersCompanyNewsItem readNewsItem(Element div) {
		Element h2 = div.select("h2").first();
		String headline = h2.text();
		String url = "https://www.reuters.com" + h2.child(0).attr("href");
		String summary = div.select("p").first().text();
		ReutersCompanyNewsItem item = new ReutersCompanyNewsItem(url, headline, summary);
		return item;
	}
	
	public static String toReutersSymbol(Company company) {
		if (company == null) {
			return null;
		}
		if (company.getExchange().equals("nasdaq")) {
			return company.getSymbol() + "." + "O";
		}
		else if (company.getExchange().equals("nyse")) {
			return company.getSymbol() + "." + "N";
		}
		return null;
	}
	
	public static List<ReutersCompanyNewsItem> readReutersCompanyNews(Company company, LocalDate localDate) throws IOException {
//		https://www.reuters.com/finance/stocks/companyNews?symbol=IBM.N&date=07232017
//		https://www.google.com/finance/company_news?q=NASDAQ%3AAMZN&startdate=2017-7-22&enddate=2017-8-01

		Set<String> headlineSet = new HashSet<>();
		List<ReutersCompanyNewsItem> newsItems = new ArrayList<>();
		
		String companyNewUrl = "https://www.reuters.com/finance/stocks/companyNews?symbol=" + toReutersSymbol(company) + 
			"&date=" + LocalDateUtils.toReutersDateString(localDate);
		Document doc = JsoupUtils.fetchDocument(companyNewUrl);
		Elements divsCompanyNews = doc.select("div[id=companyNews]");
		
		try {
			Element divTopStory = divsCompanyNews.first().select("div[class=topStory]").first();
			ReutersCompanyNewsItem item = readNewsItem(divTopStory);
			newsItems.add(item);
			
			headlineSet.add(item.getHeadline());
			String[] pieces = item.getHeadline().split(";");
			for (int i = 0; i < pieces.length; i++) {
				headlineSet.add(pieces[i].trim());
			}
		}
		// NullPointerException is thrown when this is no news for the company on that date.
		catch (NullPointerException e) {
			return new ArrayList<>();
		}
		
		Element divStories = divsCompanyNews.get(1);
		Elements divs = divStories.select("div[class=feature]");
		for (int i = 0; i < divs.size(); i++) {
			Element div = divs.get(i);
			ReutersCompanyNewsItem item = readNewsItem(div);
			if (!headlineSet.contains(item.getHeadline())) {
				newsItems.add(item);

				headlineSet.add(item.getHeadline());
				String[] pieces = item.getHeadline().split(";");
				for (int j = 0; j < pieces.length; j++) {
					headlineSet.add(pieces[j].trim());
				}				
			}
		}
		
		return newsItems;
	}
}
