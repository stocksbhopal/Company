package com.sanjoyghosh.company.api;

public interface ITableItem {

	public String[] getColumnNames();
	
	public String getData(int column);
}
