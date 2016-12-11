package com.sanjoyghosh.company.api;

import java.time.LocalDate;

import com.sanjoyghosh.company.utils.DateUtils;

public class CompanyEarnings {

	private static final String[]	columnNames = { "#", "Earnings Date", "BM/AM", "Symbol", "Name"};
	
	private int			serialNum;
	private String 		symbol;
	private String 		name;
	private LocalDate	earningsDate;
	private String 		bmOrAm;
	
	private int			companyId;
	private int			earningsDateId;
		
	
	public CompanyEarnings(String symbol, LocalDate earningsDate) {
		this.symbol = symbol;
		this.earningsDate = earningsDate;
	}
	
	
	public CompanyEarnings(String symbol, String name, LocalDate earningsDate, String bmOrAm, 
		int companyId, int earningsDateId) {
		
		this.symbol = symbol;
		this.name = name;
		this.earningsDate = earningsDate;
		this.bmOrAm = bmOrAm;
		this.companyId = companyId;
		this.earningsDateId = earningsDateId;
	}


	public static String[] getColumnNames() {
		return columnNames;
	}
	
	
	public String getData(int column) {
		switch (column) {
		case 0: return String.valueOf(serialNum);
		case 1: return earningsDate == null ? null : DateUtils.toDateString(earningsDate);
		case 2: return bmOrAm;
		case 3: return symbol;
		case 4: return name;
		default: return null;
		}
	}


	public String getSerialNum() {
		return String.valueOf(serialNum);
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


	public int getCompanyId() {
		return companyId;
	}


	public int getEarningsDateId() {
		return earningsDateId;
	}


	public void setSerialNum(int serialNum) {
		this.serialNum = serialNum;
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


	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}


	public void setEarningsDateId(int earningsDateId) {
		this.earningsDateId = earningsDateId;
	}
}
