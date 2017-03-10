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
			System.err.println("No span element list for url: " + url);
			return null;
		}
		
		Element span = spans.first();
		if (span == null) {
			System.err.println("No span element for url: " + url);
			return null;
		}

		String priceStr = span.text();
		Double price = null;
		try {
			price = priceStr.equals("unch") ? 0.00D : Double.parseDouble(priceStr);
		}
		catch (Exception e) {
			System.err.println("Could not parse as Double: " + priceStr);
			return null;
		}
		
		span = span.nextElementSibling();
		String priceChangeStr = span.text();
		Double priceChange = priceChangeStr.equals("unch") ? 0.00D : Double.parseDouble(span.text());

		span = span.nextElementSibling();
		boolean isUp = span.text().equals("▲");

		span = span.nextElementSibling();
		String percentStr = span.text();
		Double priceChangePercent = 0.00D;
		if (percentStr.length() > 0) {
			percentStr = percentStr.substring(0, percentStr.length() - 1);
			priceChangePercent = Double.parseDouble(percentStr);
		}
		
		NasdaqRealtimeQuote nrq = new NasdaqRealtimeQuote();
		nrq.setPrice(price);
		nrq.setSymbol(symbol);
		nrq.setPriceChange(isUp ? priceChange : -priceChange);
		nrq.setPriceChangePercent(isUp ? priceChangePercent : -priceChangePercent);

		return nrq;
	}
}
