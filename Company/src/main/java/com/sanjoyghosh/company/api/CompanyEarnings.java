package com.sanjoyghosh.company.api;

import java.util.Date;

import com.sanjoyghosh.company.utils.DateUtils;

public class CompanyEarnings implements ITableItem {

	private static final String[]	columnNames = { "Earnings Date", "BM/AM", "Symbol", "Name"};
	
	private String 	symbol;
	private String 	name;
	private Date	earningsDate;
	private String 	bmOrAm;
	
	private int		companyId;
	private int		earningsDateId;
	
	
	public CompanyEarnings(String symbol, String name, Date earningsDate, String bmOrAm, 
		int companyId, int earningsDateId) {
		
		this.symbol = symbol;
		this.name = name;
		this.earningsDate = earningsDate;
		this.bmOrAm = bmOrAm;
		this.companyId = companyId;
		this.earningsDateId = earningsDateId;
	}


	public String[] getColumnNames() {
		return columnNames;
	}
	
	
	public String getData(int column) {
		switch (column) {
		case 0: return earningsDate == null ? null : DateUtils.toDateString(earningsDate);
		case 1: return bmOrAm;
		case 2: return symbol;
		case 3: return name;
		default: return null;
		}
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


	public String getBmOrAm() {
		return bmOrAm;
	}


	public int getCompanyId() {
		return companyId;
	}


	public int getEarningsDateId() {
		return earningsDateId;
	}
}
