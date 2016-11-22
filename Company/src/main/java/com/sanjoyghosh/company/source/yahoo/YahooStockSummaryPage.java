package com.sanjoyghosh.company.source.yahoo;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sanjoyghosh.company.utils.JsoupUtils;

public class YahooStockSummaryPage {

	public static YahooStockSummary fetchYahooStockSummary(String symbol) throws IOException {
		String aoyUrl = "http://www.nasdaq.com/symbol/" + symbol;
		Document doc = JsoupUtils.fetchDocument(aoyUrl);
		
		Elements tables = doc.select("table[id=quotes_content_left_InfoQuotesResults]");
		if (tables == null) {
			System.err.println("No ticker for url: " + aoyUrl);
			return null;
		}
		
		try {
			Element table = tables.get(0).select("table").get(0);
			Elements trs = table.select("tr");
			
			YahooStockSummary yss = new YahooStockSummary();
			for (int i = 1; i < trs.size(); i++) {
				Elements tds = trs.get(i).select("td");
				String td1Text = tds.get(0).text().trim();
				
				if (td1Text.startsWith("Today's High /Low")) {
					Elements labels = tds.get(1).select("label");
					String highText = labels.get(0).text().substring(2);
					String lowText = labels.get(1).text().substring(2);
					// This happens for http://www.nasdaq.com/symbol/fraf
					// where the high low is "N/A / N/A"
					// We skip symbols like this.
					if (highText.equals("A")) {
						System.err.println("Today's High / Low bad for: " + symbol);
						break;
					}
					else {
						Double high = Double.parseDouble(highText);
						Double low = Double.parseDouble(lowText);
						yss.setDayRangeLow(low);
						yss.setDayRangeHigh(high);
					}
				}
				else if (td1Text.startsWith("Share Volume")) {
					String volumeText = tds.get(1).text().trim().replace(",", "");
					Integer volume = Integer.parseInt(volumeText);
					yss.setVolume(volume);
				}
				else if (td1Text.startsWith("Previous Close")) {
					String previousCloseText = tds.get(1).text().trim().substring(2);
					Double previousClose = Double.parseDouble(previousCloseText);
					yss.setPreviousClose(previousClose);
				}
				else if (td1Text.startsWith("52 Week High/Low")) {
					String td2Text = tds.get(1).text().trim();
					// Happens for http://www.nasdaq.com/symbol/bncc.
					if (td2Text.indexOf("N/A") >= 0) {
						System.err.println("52 Week High / Low bad for: " + symbol);
						continue;
					}
					String[] prices = td2Text.split("/");
					String highText = prices[0];
					highText = highText.substring(2, highText.length() - 1);
					String lowText = prices[1];
					lowText = lowText.substring(3);
					Double high = Double.parseDouble(highText);
					Double low = Double.parseDouble(lowText);
					yss.setFiftyTwoWeekRangeLow(low);
					yss.setFiftyTwoWeekRangeHigh(high);
				}
				else if (td1Text.startsWith("Market cap")) {
					String marketCapText = tds.get(1).text().substring(1).trim().replace(",", "");
					Long marketCap = Long.parseLong(marketCapText);
					yss.setMarketCap(marketCap);
				}
				else {
					continue;
				}
			}
			return yss;
		}
		catch (Exception e) {
			System.err.println("Exception for stock: " + symbol);
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		try {
			YahooStockSummary summary = YahooStockSummaryPage.fetchYahooStockSummary("YHOO");
			System.out.println("YAHOO IS: " + summary.getPrice());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
