package com.sanjoyghosh.company.api;

import java.time.LocalDate;

public class CompanyEarnings {

	private String 		symbol;
	private String 		name;
	private LocalDate	earningsDate;
	private String 		bmOrAm;
			
	
	public CompanyEarnings(String symbol, LocalDate earningsDate) {
		this.symbol = symbol;
		this.earningsDate = earningsDate;
	}
	
	
	public CompanyEarnings(String symbol, String name, LocalDate earningsDate, String bmOrAm) {
		this.symbol = symbol;
		this.name = name;
		this.earningsDate = earningsDate;
		this.bmOrAm = bmOrAm;
	}


	public String getSymbol() {
		return symbol;
	}


	public String getName() {
		return name;
	}


	public LocalDate getEarningsDate() {
		return earningsDate;
	}


	public String getBmOrAm() {
		return bmOrAm;
	}


	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setEarningsDate(LocalDate earningsDate) {
		this.earningsDate = earningsDate;
	}


	public void setBmOrAm(String bmOrAm) {
		this.bmOrAm = bmOrAm;
	}
}
