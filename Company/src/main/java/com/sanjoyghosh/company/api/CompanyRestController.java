package com.sanjoyghosh.company.api;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.EarningsDate;
import com.sanjoyghosh.company.db.model.PriceHistory;
import com.sanjoyghosh.company.utils.CompanyUtils;


@RestController
public class CompanyRestController {

	private static final SimpleDateFormat dateParser = new SimpleDateFormat("yyyyMMdd");
	
	
	@RequestMapping("/company")
	public CompanyRest company(
		@RequestParam(name="sym", required=true) String symbol,
		@RequestParam(name="spl", required=false) boolean includeStockSplits,
		@RequestParam(name="div", required=false) boolean includeDividends,
		@RequestParam(name="psd", required=false) String priceStartDate,
		@RequestParam(name="ped", required=false) String priceEndDate) {
		
		CompanyRest companyRest = new CompanyRest();		
		EntityManager entityManager = JPAHelper.getEntityManager();
		
		symbol = symbol.toUpperCase();

		Date priceStart = null;
		if (priceStartDate != null) {
			try {
				priceStart = dateParser.parse(priceStartDate);
			} 
			catch (ParseException e) {
				companyRest.addToErrorList("Bad price start date: " + priceStartDate);
			}
		}
		
		Date priceEnd = null;
		if (priceEndDate != null) {
			try {
				priceEnd = dateParser.parse(priceEndDate);
			} 
			catch (ParseException e) {
				companyRest.addToErrorList("Bad price end date: " + priceEndDate);
			}
		}
		
		if ((priceStart != null && priceEnd == null) || (priceStart == null && priceEnd != null)) {
			companyRest.addToErrorList("Both price start and price end dates need to be null or not null");
			priceStart = null;
			priceEnd = null;
		}

		Company company = CompanyUtils.fetchCompanyBySymbol(entityManager, symbol);
		if (company == null) {
			companyRest.addToErrorList("No Company for symbol: " + symbol);
		}
		
		EarningsDate lastEarningsDate = CompanyUtils.fetchLastEarningsDateForSymbol(entityManager, symbol);
				
		companyRest.setCompany(company);
		companyRest.setLastEarningsDate(lastEarningsDate);
		
		if (includeStockSplits) {
			companyRest.setStockSplitHistoryList(CompanyUtils.fetchStockSplitHistoryForSymbol(entityManager, symbol));
		}
		
		if (includeDividends) {
			companyRest.setDividendHistoryList(CompanyUtils.fetchDividendHistoryForSymbol(entityManager, symbol));
		}
		
		if (priceStart != null) {
			List<PriceHistory> priceHistoryList = CompanyUtils.fetchPriceHistoryForSymbolDateStartEnd(entityManager, symbol, new Timestamp(priceStart.getTime()), new Timestamp(priceEnd.getTime()));
			companyRest.setPriceHistoryList(priceHistoryList);
		}
		
		return companyRest;
	}
}
