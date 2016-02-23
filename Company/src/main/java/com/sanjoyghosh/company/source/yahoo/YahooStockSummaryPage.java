package com.sanjoyghosh.company.source.yahoo;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.sanjoyghosh.company.utils.JsoupUtils;
import com.sanjoyghosh.company.utils.StringUtils;

public class YahooStockSummaryPage {

	public static YahooStockSummary fetchYahooStockSummary(String symbol) throws IOException {
		boolean isIndex = symbol.startsWith("^");
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
		
		try {
			elements = doc.select("td.yfnc_tabledata1");
			Double previousClose = StringUtils.parseDouble(elements.get(0).text());
			Double open = StringUtils.parseDouble(elements.get(1).text());
			
			String daysRangeStr = elements.get(isIndex ? 2 : 7).text();
			Double[] dayRangeFloats = StringUtils.parseDoubleRange(daysRangeStr);
			
			String fiftyTwoWeekRangeStr = elements.get(isIndex ? 3 : 8).text();
			Double[] fiftyTwoWeekRangeFloats = StringUtils.parseDoubleRange(fiftyTwoWeekRangeStr);

			Double oneYearTarget = isIndex ? null : StringUtils.parseDouble(elements.get(4).text());
			Integer volume = isIndex ? null : StringUtils.parseInteger(elements.get(9).text());
			Integer threeMonthAverageVolume = isIndex ? null : StringUtils.parseInteger(elements.get(10).text());
			
			String marketCapStr = isIndex ? null : elements.get(11).text();		
			Long marketCap = isIndex ? null : StringUtils.parseLongWithBMK(marketCapStr);
			
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
		catch (IndexOutOfBoundsException e) {
			System.err.println("Cannot get Stock Summary for: " + aoyUrl);
			e.printStackTrace();
		}
		return null;
	}
}
