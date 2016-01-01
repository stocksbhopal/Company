package com.sanjoyghosh.company.source.yahoo;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sanjoyghosh.company.db.JsoupUtils;

public class YahooAnalystOpinionPage {

	public static YahooAnalystOpinion fetchAnalystOpinionYahoo(String symbol) throws IOException {
		String aoyUrl = "http://finance.yahoo.com/q/ao?s=" + symbol + "+Analyst+Opinion";
		Document doc = JsoupUtils.fetchDocument(aoyUrl);
		Elements elements = doc.select("td.yfnc_tabledata1");

		if (elements.size() > 0) {
			YahooAnalystOpinion yao = new YahooAnalystOpinion();
			
			for (int i = 0; i < 8; i++) {
				Element element = elements.get(i);
				String text = element.text().trim();
				if (text.equals("N/A")) {
					return null;
				}
				text = text.replaceAll(",", "");
				try {
					switch(i) {
						case 0: yao.setMeanRecommendationThisWeek(Double.parseDouble(text)); break;
						case 1: yao.setMeanRecommendationLastWeek(Double.parseDouble(text)); break;
						case 2: yao.setChangeMeanRecommendation(Double.parseDouble(text)); break;
						case 3: yao.setMeanTarget(Double.parseDouble(text)); break;
						case 4: yao.setMedianTarget(Double.parseDouble(text)); break;
						case 5: yao.setHighTarget(Double.parseDouble(text)); break;
						case 6: yao.setLowTarget(Double.parseDouble(text)); break;
						case 7: yao.setNumberOfBrokers(Integer.parseInt(text)); break;
					}
				}
				catch (NumberFormatException e) {
					e.printStackTrace();
					return null;
				}
			}
	
			for (int i = elements.size() - 20; i < elements.size(); i++) {
				Element element = elements.get(i);
				String text = element.text().trim();
				Integer intValue = Integer.parseInt(text);
				int j = i - (elements.size() - 20);
				
				try {
					switch(j) {
						case 0: yao.setStrongBuyCurrentMonth(intValue); break;
						case 1: yao.setStrongBuyLastMonth(intValue); break;
						case 2: yao.setStrongBuyTwoMonthsAgo(intValue); break;
						case 3: yao.setStrongBuyThreeMonthsAgo(intValue); break;
						case 4: yao.setBuyCurrentMonth(intValue); break;
						case 5: yao.setBuyLastMonth(intValue); break;
						case 6: yao.setBuyTwoMonthsAgo(intValue); break;
						case 7: yao.setBuyThreeMonthsAgo(intValue); break;
						case 8: yao.setHoldCurrentMonth(intValue); break;
						case 9: yao.setHoldLastMonth(intValue); break;
						case 10: yao.setHoldTwoMonthsAgo(intValue); break;
						case 11: yao.setHoldThreeMonthsAgo(intValue); break;
						case 12: yao.setUnderperformCurrentMonth(intValue); break;
						case 13: yao.setUnderperformLastMonth(intValue); break;
						case 14: yao.setUnderperformTwoMonthsAgo(intValue); break;
						case 15: yao.setUnderperformThreeMonthsAgo(intValue); break;
						case 16: yao.setSellCurrentMonth(intValue); break;
						case 17: yao.setSellLastMonth(intValue); break;
						case 18: yao.setSellTwoMonthsAgo(intValue); break;
						case 19: yao.setSellThreeMonthsAgo(intValue); break;
					}
				}
				catch (NumberFormatException e) {
					e.printStackTrace();
					return null;
				}
			}
			return yao;
		}
		return null;
	}
}
