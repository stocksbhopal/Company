package com.sanjoyghosh.company.earnings.intent;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;

public interface InterfaceIntent {

    public static final String ATTR_LAST_INTENT = "ATTR_LAST_INTENT";
    public static final String ATTR_SYMBOL = "ATTR_SYMBOL";

    public static final String SLOT_COMPANY = "company";
    public static final String SLOT_SPELLING_ONE = "spellingOne";
    public static final String SLOT_SPELLING_TWO = "spellingTwo";
    public static final String SLOT_SPELLING_THREE = "spellingThree";
    public static final String SLOT_SPELLING_FOUR = "spellingFour";
    public static final String SLOT_SPELLING_FIVE = "spellingFive";
    public static final String SLOT_SPELLING_SIX = "spellingSix";
    
    public static final String SLOT_DATE = "date";
    public static final String SLOT_MARKET_INDEX = "market_index";
    
    public static final String INTENT_CREATE_MERRILL_LYNCH_STOCK_LIST = "CreateMerrillLynchStockList";
    public static final String INTENT_CREATE_MORGAN_STANLEY_STOCK_LIST = "CreateMorganStanleyStockList";
    public static final String INTENT_CREATE_JP_MORGAN_STOCK_LIST = "CreateJPMorganStockList";
    public static final String INTENT_CREATE_PERSONAL_STOCK_LIST = "CreatePersonalStockList";
    
    public static final String INTENT_GET_MY_STOCKS = "GetMyStocks";
    public static final String INTENT_GET_MY_STOCKS_WITH_EARNINGS = "GetMyStocksWithEarnings";
    
    public static final String INTENT_GET_STOCK_PRICE = "GetStockPrice";
    public static final String INTENT_GET_STOCK_PRICE_SPELL_ONE = "GetStockPriceSpellOne";
    public static final String INTENT_GET_STOCK_PRICE_SPELL_TWO = "GetStockPriceSpellTwo";
    public static final String INTENT_GET_STOCK_PRICE_SPELL_THREE = "GetStockPriceSpellThree";
    public static final String INTENT_GET_STOCK_PRICE_SPELL_FOUR = "GetStockPriceSpellFour";
    public static final String INTENT_GET_STOCK_PRICE_SPELL_FIVE = "GetStockPriceSpellFive";
    public static final String INTENT_GET_STOCK_PRICE_SPELL_SIX = "GetStockPriceSpellSix";

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
    
    public static final String DYNDB_COL_USER_ID = "userId";
    public static final String DYNDB_COL_SYMBOL = "symbol";
    public static final String DYNDB_COL_FULL_NAME = "fullName";
    
    
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException;
}
