package com.sanjoyghosh.company.api;

import java.util.Date;

public class CompanyEarnings {

	private String 	symbol;
	private String 	name;
	private Date	earningsDate;
	
	private int		companyId;
	private int		earningsDateId;
	
	
	public CompanyEarnings(String symbol, String name, Date earningsDate, int companyId, int earningsDateId) {
		super();
		this.symbol = symbol;
		this.name = name;
		this.earningsDate = earningsDate;
		this.companyId = companyId;
		this.earningsDateId = earningsDateId;
	}


	public String getSymbol() {
		return symbol;
	}


	public String getName() {
		return name;
	}


	public Date getEarningsDate() {
		return earningsDate;
	}
}
