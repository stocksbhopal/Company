package com.sanjoyghosh.company.db;

public class PortfolioItemData {

	private String symbol;
	private String speechName;
	private double quantity;
	private double valueChangeDollars;
	private double priceChangePercent;

	
	public PortfolioItemData(String symbol, String speechName, Double quantity) {
		this.symbol = symbol;
		this.speechName = speechName;
		this.quantity = quantity;
	}


	public String getSpeechName() {
		return speechName;
	}


	public double getQuantity() {
		return quantity;
	}


	public String getSymbol() {
		return symbol;
	}


	public double getValueChangeDollars() {
		return valueChangeDollars;
	}


	public void setValueChangeDollars(double valueChangeDollars) {
		this.valueChangeDollars = valueChangeDollars;
	}


	public double getPriceChangePercent() {
		return priceChangePercent;
	}


	public void setPriceChangePercent(double priceChangePercent) {
		this.priceChangePercent = priceChangePercent;
	}
}
