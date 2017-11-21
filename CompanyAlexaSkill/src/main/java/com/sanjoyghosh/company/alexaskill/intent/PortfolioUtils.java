package com.sanjoyghosh.company.alexaskill.intent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sanjoyghosh.company.alexaskill.source.nasdaq.NasdaqRealtimeQuote;
import com.sanjoyghosh.company.alexaskill.source.nasdaq.NasdaqRealtimeQuoteReader;
import com.sanjoyghosh.company.dynamodb.helper.CompanyMatcher;
import com.sanjoyghosh.company.dynamodb.model.Portfolio;

public class PortfolioUtils {

    private static final Logger logger = Logger.getLogger(IntentGetStockPrice.class.getName());

    
    public static double getNetValueChange(List<PortfolioItemData> portfolioItemDataList, IntentResult intentResult) {
    	double valueChange = 0.00;
		for (PortfolioItemData portfolioItemData : portfolioItemDataList) {
			String symbol = portfolioItemData.getSymbol();
			NasdaqRealtimeQuote quote = null;
			try {
				quote = NasdaqRealtimeQuoteReader.fetchNasdaqStockSummary(portfolioItemData.getSymbol());
			} 
			catch (IOException e) {
				intentResult.addSymbolWithException(symbol, e);
				logger.log(Level.SEVERE, "Exception in reading Nasdaq quote for " + symbol, e);
			}
			
			if (quote == null) {
				intentResult.addNullQuoteSymbol(symbol);
				logger.log(Level.SEVERE, "Null quote read from Nasdaq for " + symbol);
			}
			else {
				portfolioItemData.setPrice(quote.getPrice());
				portfolioItemData.setPriceChange(quote.getPriceChange());
				portfolioItemData.setPriceChangePercent(quote.getPriceChangePercent());
				portfolioItemData.setValueChangeDollars(portfolioItemData.getQuantity() * quote.getPriceChange());
				valueChange += portfolioItemData.getValueChangeDollars();
			}
		}
		
		return valueChange;
    }

    
    public static List<PortfolioItemData> getPortfolioItemListValueChange(List<Portfolio> portfolioList, IntentResult intentResult) {
	    	if (portfolioList == null || portfolioList.isEmpty()) {
	    		return null;
	    	}

    		List<PortfolioItemData> portfolioItemDataList = new ArrayList<>(portfolioList.size());
		for (Portfolio portfolio : portfolioList) {
			String symbol = portfolio.getSymbol();
			String name = CompanyMatcher.getCompanyNameBySymbol(symbol);
			
			NasdaqRealtimeQuote quote = null;
			try {
				quote = NasdaqRealtimeQuoteReader.fetchNasdaqStockSummary(symbol);
			} 
			catch (IOException e) {
				intentResult.addSymbolWithException(symbol, e);
				logger.log(Level.SEVERE, "Exception in reading Nasdaq quote for " + symbol, e);
			}
			
			if (quote == null) {
				intentResult.addNullQuoteSymbol(symbol);
				logger.log(Level.SEVERE, "Null quote read from Nasdaq for " + symbol);
			}
			else {
				PortfolioItemData portfolioItemData = new PortfolioItemData(symbol, name, 
					name, quote.getPrice(), quote.getPriceChange(), quote.getPriceChangePercent(), 
					portfolio.getQuantity());
				portfolioItemDataList.add(portfolioItemData);
			}
		}
		
		return portfolioItemDataList;
    }
    
   
    /**
     * 
     * @param portfolioItemDataList
     * @param sortByValueChange True if sort by valueChange.  False if sort by priceChangePercent.
     * @param sortAscending  True if sort ascending.  False if sort descending.
     */
    public static void sortPortfolioItemDataList(List<PortfolioItemData> portfolioItemDataList, boolean sortByValueChange, boolean sortAscending) {
    		Collections.sort(portfolioItemDataList, new Comparator<PortfolioItemData>() {
			@Override
			public int compare(PortfolioItemData o1, PortfolioItemData o2) {
				if (sortByValueChange) {
					int comparison = new Double(o1.getValueChangeDollars()).compareTo(new Double(o2.getValueChangeDollars()));
					return sortAscending ? comparison : -comparison;
				}
				else {
					int comparison = new Double(o1.getPriceChangePercent()).compareTo(new Double(o2.getPriceChangePercent()));		
					return sortAscending ? comparison : -comparison;
				}
			}
		});
    }
}
