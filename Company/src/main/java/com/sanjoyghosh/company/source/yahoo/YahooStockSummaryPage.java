package com.sanjoyghosh.company.source.yahoo;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.sanjoyghosh.company.db.JsoupUtils;
import com.sanjoyghosh.company.db.StringUtils;

public class YahooStockSummaryPage {

	public static YahooStockSummary fetchYahooStockSummary(String symbol) throws IOException {
		String aoyUrl = "http://finance.yahoo.com/q?s=" + symbol;
		Document doc = JsoupUtils.fetchDocument(aoyUrl);
		
		Elements elements = doc.select("span.time_rtq_ticker");
		if (elements == null || elements.text() == null || elements.text().length() == 0) {
			System.err.println("No ticker for url: " + aoyUrl);
			return null;
		}

		Double price = 0.0D;
		try {price = StringUtils.parseDouble(elements.text());}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		elements = doc.select("td.yfnc_tabledata1");
		Double previousClose = StringUtils.parseDouble(elements.get(0).text());
		Double open = StringUtils.parseDouble(elements.get(1).text());
		Double oneYearTarget = StringUtils.parseDouble(elements.get(4).text());
		
		String daysRangeStr = elements.get(7).text();
		Double[] dayRangeFloats = StringUtils.parseDoubleRange(daysRangeStr);
		
		String fiftyTwoWeekRangeStr = elements.get(8).text();
		Double[] fiftyTwoWeekRangeFloats = StringUtils.parseDoubleRange(fiftyTwoWeekRangeStr);
		
		Integer volume = StringUtils.parseInteger(elements.get(9).text());
		Integer threeMonthAverageVolume = StringUtils.parseInteger(elements.get(10).text());
		
		String marketCapStr = elements.get(11).text();		
		Long marketCap = StringUtils.parseLongWithBMK(marketCapStr);
		
		YahooStockSummary yss = new YahooStockSummary();
		yss.setPrice(price);
		yss.setPreviousClose(previousClose);
		yss.setOpen(open);
		yss.setOneYearTarget(oneYearTarget);
		if (dayRangeFloats != null) {
			yss.setDayRangeLow(dayRangeFloats[0]);
			yss.setDayRangeHigh(dayRangeFloats[1]);
		}
		if (fiftyTwoWeekRangeFloats != null) {
			yss.setFiftyTwoWeekRangeLow(fiftyTwoWeekRangeFloats[0]);
			yss.setFiftyTwoWeekRangeHigh(fiftyTwoWeekRangeFloats[1]);
		}
		yss.setVolume(volume);
		yss.setThreeMonthAverageVolume(threeMonthAverageVolume);
		yss.setMarketCap(marketCap);
		yss.setMarketCapBM(marketCapStr);
		
		return yss;
	}
}
