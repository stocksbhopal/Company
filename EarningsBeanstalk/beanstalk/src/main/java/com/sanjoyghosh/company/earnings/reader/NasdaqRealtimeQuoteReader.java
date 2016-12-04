package com.sanjoyghosh.company.earnings.reader;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sanjoyghosh.company.earnings.utils.JsoupUtils;

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

		Double price = Double.parseDouble(span.text());
		
		span = span.nextElementSibling();
		Double priceChange = Double.parseDouble(span.text());

		span = span.nextElementSibling();
		boolean isUp = span.text().equals("▲");

		span = span.nextElementSibling();
		String percentStr = span.text();
		percentStr = percentStr.substring(0, percentStr.length() - 1);
		Double priceChangePercent = Double.parseDouble(percentStr);
		
		NasdaqRealtimeQuote nrq = new NasdaqRealtimeQuote();
		nrq.setPrice(price);
		nrq.setPriceChange(isUp ? priceChange : -priceChange);
		nrq.setPriceChangePercent(isUp ? priceChangePercent : -priceChangePercent);

		return nrq;
	}
	
	
	public static void main(String[] args) {
		try {
			NasdaqRealtimeQuote nrq = NasdaqRealtimeQuoteReader.fetchNasdaqStockSummary("armh");
			System.out.println(nrq);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
