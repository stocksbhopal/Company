package com.sanjoyghosh.company.yahoo;

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

		float price = 0.0F;
		try {price = StringUtils.parseFloat(elements.text());}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		elements = doc.select("td.yfnc_tabledata1");
		Float previousClose = StringUtils.parseFloat(elements.get(0).text());
		Float open = StringUtils.parseFloat(elements.get(1).text());
		Float oneYearTarget = StringUtils.parseFloat(elements.get(4).text());
		
		String daysRangeStr = elements.get(7).text();
		Float[] dayRangeFloats = StringUtils.parseFloatRange(daysRangeStr);
		
		String fiftyTwoWeekRangeStr = elements.get(8).text();
		Float[] fiftyTwoWeekRangeFloats = StringUtils.parseFloatRange(fiftyTwoWeekRangeStr);
		
		Integer volume = StringUtils.parseInteger(elements.get(9).text());
		Integer threeMonthAverageVolume = StringUtils.parseInteger(elements.get(10).text());
		
		String marketCapStr = elements.get(11).text();		
		Long marketCap = StringUtils.parseLongWithBM(marketCapStr);
		
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
		
		return yss;
	}
}
