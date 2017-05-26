package com.sanjoyghosh.company.earnings.intent;

import java.io.IOException;
import java.util.ArrayList;
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
    
    
    public static List<PortfolioItemData> getPortfolioValueChange(Portfolio portfolio, IntentResult intentResult) {
    	if (portfolio == null || portfolio.getPortfolioItemList() == null || portfolio.getPortfolioItemList().isEmpty()) {
    		return null;
    	}
    	
    	List<PortfolioItem> portfolioItemList = portfolio.getPortfolioItemList();
    	List<PortfolioItemData> portfolioItemDataList = getPortfolioItemListValueChange(portfolioItemList, intentResult);
    	return portfolioItemDataList;
    }
}
