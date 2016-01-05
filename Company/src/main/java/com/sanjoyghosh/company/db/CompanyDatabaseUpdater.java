package com.sanjoyghosh.company.db;

import com.sanjoyghosh.company.source.yahoo.YahooCompanyUpdater;
import com.sanjoyghosh.company.source.yahoo.YahooEarningsCalendarReader;
import com.sanjoyghosh.company.source.yahoo.YahooHistoricalPricesReader;

public class CompanyDatabaseUpdater {

	public static void main(String[] args) {
		YahooCompanyUpdater ycu = new YahooCompanyUpdater();
		ycu.updateMarketCapForAllCompanies();

		YahooEarningsCalendarReader yecr = new YahooEarningsCalendarReader();
		yecr.readEarningsCalendarforWeek();		

		YahooHistoricalPricesReader yhpr = new YahooHistoricalPricesReader();
		yhpr.readAllHistoricalPrices();
		
		System.exit(0);
	}
}
