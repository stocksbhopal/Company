package com.sanjoyghosh.company.earnings.intent;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;

public interface InterfaceIntent {

    public static final String ATTR_LAST_INTENT = "ATTR_LAST_INTENT";
    public static final String ATTR_SYMBOL = "ATTR_SYMBOL";

    public static final String SLOT_COMPANY = "company";    
    public static final String SLOT_DATE = "date";
    public static final String SLOT_QUANTITY = "quantity";

    public static final String INTENT_CREATE_STOCK_ON_LIST = "CreateStockOnList";
    public static final String INTENT_READ_STOCK_ON_LIST = "ReadStockOnList";
    public static final String INTENT_UPDATE_STOCK_ON_LIST = "UpdateStockOnList";
    public static final String INTENT_DELETE_STOCK_ON_LIST = "DeleteStockOnList";
    public static final String INTENT_LIST_STOCKS_ON_LIST = "ListStocksOnList";
    
    public static final String INTENT_GET_STOCK_PRICE = "GetStockPrice";

    public static final String INTENT_GET_MY_STOCKS = "GetMyStocks";
    public static final String INTENT_GET_MY_STOCKS_WITH_EARNINGS = "GetMyStocksWithEarnings";    

    public static final String INTENT_ADD_COMPANY = "AddCompany";
    public static final String INTENT_LIST_COMPANIES = "ListCompanies";
    public static final String INTENT_LIST_EARNINGS_MY_BY = "ListEarningsMyBy";
    public static final String INTENT_LIST_INDEX_EARNINGS = "ListIndexEarnings";
    public static final String INTENT_LIST_EARNINGS_NEXT = "ListEarningsNext";
    public static final String INTENT_LIST_INDEX_EARNINGS_NEXT = "ListIndexEarningsNext";
    public static final String INTENT_LIST_EARNINGS_MY_NEXT = "ListEarningsMyNext";
    public static final String INTENT_LIST_INDEX_EARNINGS_MY_NEXT = "ListIndexEarningsMyNext";
    public static final String INTENT_MY_STOCKS_STATUS = "MyStocksStatus";
    public static final String INTENT_MY_STOCKS_GAINERS = "MyStocksGainers";
    public static final String INTENT_MY_STOCKS_LOSERS = "MyStocksLosers";
    public static final String INTENT_UPDATE_PRICES = "UpdatePrices";
    
    public static final String DYNDB_TABLE_MY_STOCKS = "MyStocks";
    public static final String DYNDB_TABLE_WATCH_LIST = "WatchList";
        
    
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException;
}
