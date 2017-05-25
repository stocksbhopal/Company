package com.sanjoyghosh.company.earnings.intent;

import java.io.IOException;
import java.util.List;

import com.sanjoyghosh.company.db.PortfolioItemData;
import com.sanjoyghosh.company.db.model.PortfolioItem;
import com.sanjoyghosh.company.source.nasdaq.NasdaqRealtimeQuote;
import com.sanjoyghosh.company.source.nasdaq.NasdaqRealtimeQuoteReader;

public class PortfolioUtils {

    private double getNetValueChange(List<PortfolioItem> portfolioItemList) {
    	double valueChange = 0.00;
		for (PortfolioItem portfolioItem : portfolioItemList) {
			NasdaqRealtimeQuote quote = null;
			try {
				quote = NasdaqRealtimeQuoteReader.fetchNasdaqStockSummary(portfolioItem.getCompany().getSymbol());
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			if (quote != null) {
				portfolioItemData.setPriceChangePercent(quote.getPriceChangePercent());
				portfolioItemData.setValueChangeDollars(portfolioItemData.getQuantity() * quote.getPriceChange());
				valueChange += portfolioItemData.getValueChangeDollars();
			}
		}
		return valueChange;
    }

}
