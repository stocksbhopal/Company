package com.sanjoyghosh.company.api;

import java.util.ArrayList;
import java.util.List;

import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.EarningsDate;
import com.sanjoyghosh.company.db.model.PriceHistory;

public class CompanyRest {

	private Company 			company;
	private EarningsDate 		lastEarningsDate;
	private List<PriceHistory> 	priceHistoryList;
	private List<String> 		errorList;
	
	
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	
	
	public EarningsDate getLastEarningsDate() {
		return lastEarningsDate;
	}
	public void setLastEarningsDate(EarningsDate lastEarningsDate) {
		this.lastEarningsDate = lastEarningsDate;
	}
	
	
	public List<String> getErrorList() {
		return errorList;
	}
	public void addToErrorList(String error) {
		if (errorList == null) {
			errorList = new ArrayList<String>();
		}
		errorList.add(error);
	}
	
	
	public List<PriceHistory> getPriceHistoryList() {
		return priceHistoryList;
	}
	public void setPriceHistoryList(List<PriceHistory> priceHistoryList) {
		this.priceHistoryList = priceHistoryList;
	}
}
