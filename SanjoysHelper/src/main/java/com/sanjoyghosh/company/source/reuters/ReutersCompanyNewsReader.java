package com.sanjoyghosh.company.source.reuters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.utils.JsoupUtils;

public class ReutersCompanyNewsReader {

	private static ReutersCompanyNewsItem readNewsItem(Element div) {
		Element h2 = div.select("h2").first();
		String headline = h2.text();
		String url = "https://www.reuters.com" + h2.child(0).attr("href");
		String summary = div.select("p").first().text();
		ReutersCompanyNewsItem item = new ReutersCompanyNewsItem(url, headline, summary);
		System.out.println(url + "  " + headline);
		return item;
	}
	
	private static List<ReutersCompanyNewsItem> readReutersCompanyHeadlines(Company company) throws IOException {
//		https://www.reuters.com/finance/stocks/companyNews?symbol=IBM.N&date=07232017
//		https://www.google.com/finance/company_news?q=NASDAQ%3AAMZN&startdate=2017-7-22&enddate=2017-8-01
		
		String companyNewUrl = "https://www.reuters.com/finance/stocks/companyNews?symbol=" + "AMZN.O" + "&date=07272017";
		Document doc = JsoupUtils.fetchDocument(companyNewUrl);
		Elements divsCompanyNews = doc.select("div[id=companyNews]");
		List<ReutersCompanyNewsItem> newsItems = new ArrayList<>();
		
		Element divTopStory = divsCompanyNews.first().select("div[class=topStory]").first();
		ReutersCompanyNewsItem item = readNewsItem(divTopStory);
		newsItems.add(item);
		
		Element divStories = divsCompanyNews.get(1);
		Elements divs = divStories.select("div[class=feature]");
		for (int i = 0; i < divs.size(); i++) {
			Element div = divs.get(i);
			item = readNewsItem(div);
			newsItems.add(item);
		}
		
		return newsItems;
	}

	
	public static void main(String[] args) {
		try {
			List<ReutersCompanyNewsItem> newsItems = readReutersCompanyHeadlines(null);
			System.out.println(newsItems.size());
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}
