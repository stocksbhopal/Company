package com.sanjoyghosh.company.source.nasdaq;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sanjoyghosh.company.utils.JsoupUtils;

public class NasdaqRealtimeQuoteReader {

	public static NasdaqRealtimeQuote fetchNasdaqStockSummary(String symbol) throws IOException {
		String url = "http://www.nasdaq.com/symbol/" + symbol + "/real-time";
		Document doc = JsoupUtils.fetchDocument(url);
		
		Elements spans = doc.select("span[id=quotes_content_left__LastSale]");
		if (spans == null) {
			System.err.println("No quote for url: " + url);
			return null;
		}
		Element span = spans.first();
		System.out.println(span.text());
		
		span = span.nextElementSibling();
		System.out.println(span.text());

		span = span.nextElementSibling();
		System.out.println(span.text());
		System.out.println(span.text().equals("▲"));

		span = span.nextElementSibling();
		System.out.println(span.text());

		return null;
		/*
		try {
			Element table = tables.get(0).select("table").get(0);
			Elements trs = table.select("tr");
			
			NasdaqRealtimeQuote nss = new NasdaqRealtimeQuote();
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
						nss.setDayRangeLow(low);
						nss.setDayRangeHigh(high);
					}
				}
				else if (td1Text.startsWith("Share Volume")) {
					String volumeText = tds.get(1).text().trim().replace(",", "");
					Integer volume = Integer.parseInt(volumeText);
					nss.setVolume(volume);
				}
				else if (td1Text.startsWith("Previous Close")) {
					String previousCloseText = tds.get(1).text().trim().substring(2);
					Double previousClose = Double.parseDouble(previousCloseText);
					nss.setPreviousClose(previousClose);
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
					nss.setFiftyTwoWeekRangeLow(low);
					nss.setFiftyTwoWeekRangeHigh(high);
				}
				else if (td1Text.startsWith("Market cap")) {
					String marketCapText = tds.get(1).text().substring(1).trim().replace(",", "");
					Long marketCap = Long.parseLong(marketCapText);
					nss.setMarketCap(marketCap);
				}
				else {
					continue;
				}
			}
			return nss;
		}
		catch (Exception e) {
			System.err.println("Exception for stock: " + symbol);
			e.printStackTrace();
			return null;
		}
		*/
	}
	
	
	public static void main(String[] args) {
		try {
			NasdaqRealtimeQuote nrq = NasdaqRealtimeQuoteReader.fetchNasdaqStockSummary("veev");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
