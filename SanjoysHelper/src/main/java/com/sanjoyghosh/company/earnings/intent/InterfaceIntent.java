package com.sanjoyghosh.company.earnings.intent;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;

public interface InterfaceIntent {

	// Make sure the first letter of each ATTR_ is unique to serialize for logging.
    public static final String ATTR_LAST_INTENT = "LAST_INTENT";
    public static final String ATTR_SYMBOL = "SYMBOL";
    public static final String ATTR_QUANTITY = "QUANTITY";

	// Make sure the first letter of each SLOT_ is unique to serialize for logging.
    public static final String SLOT_COMPANY = "company";    
    public static final String SLOT_DATE = "date";
    public static final String SLOT_QUANTITY = "quantity";

    public static final String INTENT_AMAZON_CANCEL_INTENT = "AMAZON.CancelIntent";
    public static final String INTENT_AMAZON_STOP_INTENT = "AMAZON.StopIntent";
    public static final String INTENT_AMAZON_YES_INTENT = "AMAZON.YesIntent";
    public static final String INTENT_AMAZON_NO_INTENT = "AMAZON.NoIntent";
    public static final String INTENT_AMAZON_HELP_INTENT = "AMAZON.HelpIntent";
    
    public static final String INTENT_CREATE_STOCK_ON_LIST = "CreateStockOnList";
    public static final String INTENT_READ_STOCK_ON_LIST = "ReadStockOnList";
    public static final String INTENT_UPDATE_STOCK_ON_LIST = "UpdateStockOnList";
    public static final String INTENT_DELETE_STOCK_ON_LIST = "DeleteStockOnList";
    public static final String INTENT_LIST_STOCKS_ON_LIST = "ListStocksOnList";
    public static final String INTENT_CLEAR_STOCKS_ON_LIST = "ClearStocksOnList";

    public static final String INTENT_UPDATE_PRICES_ON_LIST = "UpdatePricesOnList";
    public static final String INTENT_TODAY_PERFORMANCE = "TodayPerformance";
    public static final String INTENT_TODAY_TOP_GAINERS_DOLLARS = "TodayTopGainersDollars";
    public static final String INTENT_TODAY_TOP_GAINERS_PERCENTAGE = "TodayTopGainersPercentage";
    public static final String INTENT_TODAY_TOP_LOSERS_DOLLARS = "TodayTopLosersDollars";
    public static final String INTENT_TODAY_TOP_LOSERS_PERCENTAGE = "TodayTopLosersPercentage";
    
    public static final String INTENT_EARNINGS_ON_LIST = "GetEarningsOnList";    

    public static final String INTENT_GET_STOCK_PRICE = "GetStockPrice";


    /* Ignore intents below. */
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
    
    
	public SpeechletResponse onIntent(IntentRequest request, Session session, IntentResult intentResult) throws SpeechletException;
}
