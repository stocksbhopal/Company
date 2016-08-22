package com.sanjoyghosh.company.source.yahoo;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sanjoyghosh.company.utils.JsoupUtils;

public class YahooAnalystOpinionPage {

	public static YahooAnalystOpinion fetchAnalystOpinionYahoo(String symbol) throws IOException {
		String aoyUrl = "http://www.nasdaq.com/symbol/" + symbol;
		Document doc = JsoupUtils.fetchDocument(aoyUrl);
		
		Elements elements = doc.select("a[href=http://www.nasdaq.com/investing/glossary/#MeanRec]");

		if (elements.size() > 0) {
			YahooAnalystOpinion yao = new YahooAnalystOpinion();			
			Element element = elements.get(0).parent().nextElementSibling();
			String text = element.text().trim();
			try {
				if (text.length() > 0) {
					yao.setMeanRecommendationThisWeek(Double.parseDouble(text));
				}
			}
			catch (NumberFormatException e) {
				e.printStackTrace();
				return null;
			}	
			return yao;
		}
		return null;
	}
}
