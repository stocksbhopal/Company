package com.sanjoyghosh.company.source.reuters;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sanjoyghosh.company.db.model.Company;
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
	
	public static List<ReutersCompanyNewsItem> readReutersCompanyNews(Company company, LocalDate localDate) throws IOException {
//		https://www.reuters.com/finance/stocks/companyNews?symbol=IBM.N&date=07232017
//		https://www.google.com/finance/company_news?q=NASDAQ%3AAMZN&startdate=2017-7-22&enddate=2017-8-01
		
		String companyNewUrl = "https://www.reuters.com/finance/stocks/companyNews?symbol=" + Utils.toReutersSymbol(company) + 
			"&date=" + LocalDateUtils.toReutersDateString(localDate);
		Document doc = JsoupUtils.fetchDocument(companyNewUrl);
		Elements divsCompanyNews = doc.select("div[id=companyNews]");
		List<ReutersCompanyNewsItem> newsItems = new ArrayList<>();
		
		try {
			Element divTopStory = divsCompanyNews.first().select("div[class=topStory]").first();
			ReutersCompanyNewsItem item = readNewsItem(divTopStory);
			newsItems.add(item);
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
			newsItems.add(item);
		}
		
		return newsItems;
	}
}
