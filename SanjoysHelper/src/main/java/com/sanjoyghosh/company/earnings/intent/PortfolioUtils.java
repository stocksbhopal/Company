package com.sanjoyghosh.company.earnings.intent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sanjoyghosh.company.db.PortfolioItemData;
import com.sanjoyghosh.company.db.model.Portfolio;
import com.sanjoyghosh.company.db.model.PortfolioItem;
import com.sanjoyghosh.company.source.nasdaq.NasdaqRealtimeQuote;
import com.sanjoyghosh.company.source.nasdaq.NasdaqRealtimeQuoteReader;

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

    
    public static List<PortfolioItemData> getPortfolioItemListValueChange(List<PortfolioItem> portfolioItemList, IntentResult intentResult) {
    	if (portfolioItemList == null || portfolioItemList.isEmpty()) {
    		return null;
    	}

    	List<PortfolioItemData> portfolioItemDataList = new ArrayList<>(portfolioItemList.size());
		for (PortfolioItem portfolioItem : portfolioItemList) {
			NasdaqRealtimeQuote quote = null;
			String symbol = portfolioItem.getCompany().getSymbol();
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
				PortfolioItemData portfolioItemData = new PortfolioItemData(symbol, portfolioItem.getCompany().getSpeechName(), 
					quote.getPrice(), quote.getPriceChange(), quote.getPriceChangePercent(), portfolioItem.getQuantity());
				portfolioItemDataList.add(portfolioItemData);
			}
		}
		
		return portfolioItemDataList;
    }
    
    
    public static boolean updatePortfolioPrices(Portfolio portfolio, IntentResult intentResult) {
    	if (portfolio == null || portfolio.getPortfolioItemList() == null || portfolio.getPortfolioItemList().isEmpty()) {
    		return true;
    	}
    	
    	int numGainers = 0;
    	int numLosers = 0;
    	double netValueChange = 0.00D;
    	
    	List<PortfolioItem> portfolioItemList = portfolio.getPortfolioItemList();
    	for (PortfolioItem portfolioItem : portfolioItemList) {
			NasdaqRealtimeQuote quote = null;
			String symbol = portfolioItem.getCompany().getSymbol();
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
				portfolioItem.setPrice(quote.getPrice());
				portfolioItem.setPriceChange(quote.getPriceChange());
				portfolioItem.setPriceChangePercent(quote.getPriceChangePercent());
				portfolioItem.setValueChange(portfolioItem.getQuantity() * quote.getPriceChange());
				
				if (quote.getPriceChange() >= 0.00) {
					numGainers++;
				}
				else {
					numLosers++;
				}
				netValueChange += portfolioItem.getValueChange();
			}
		}
    	
    	portfolio.setNetValueChange(netValueChange);
    	portfolio.setNumGainers(numGainers);
    	portfolio.setNumLosers(numLosers);
		
    	return true;
    }
    

    /**
     * 
     * @param portfolioItemDataList
     * @param sortByValueChange True if sort by valueChange.  False if sort by priceChangePercent.
     * @param sortAscending  True if sort ascending.  False if sort descending.
     */
    public static void sortPortfolioItemList(List<PortfolioItem> portfolioItemList, boolean sortByValueChange, boolean sortAscending) {
    	Collections.sort(portfolioItemList, new Comparator<PortfolioItem>() {
			@Override
			public int compare(PortfolioItem o1, PortfolioItem o2) {
				if (sortByValueChange) {
					int comparison = new Double(o1.getValueChange()).compareTo(new Double(o2.getValueChange()));
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
