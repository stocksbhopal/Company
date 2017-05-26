package com.sanjoyghosh.company.db;

public class PortfolioItemData {

	private String symbol;
	private String speechName;
	private Double price;
	private Double priceChange;
	private double quantity;
	private double valueChangeDollars;
	private double priceChangePercent;

	
	public PortfolioItemData(String symbol, String speechName, 
		Double price, Double priceChange, Double priceChangePercent, Double quantity) {
		
		this.symbol = symbol;
		this.speechName = speechName;
		this.price = price;
		this.priceChange = priceChange;
		this.priceChangePercent = priceChangePercent;
		this.quantity = quantity;
		this.valueChangeDollars = priceChange * quantity;
	}


	public String getSymbol() {
		return symbol;
	}


	public String getSpeechName() {
		return speechName;
	}


	public Double getPrice() {
		return price;
	}


	public Double getPriceChange() {
		return priceChange;
	}


	public double getQuantity() {
		return quantity;
	}


	public double getValueChangeDollars() {
		return valueChangeDollars;
	}


	public double getPriceChangePercent() {
		return priceChangePercent;
	}


	public void setPrice(Double price) {
		this.price = price;
	}


	public void setPriceChange(Double priceChange) {
		this.priceChange = priceChange;
	}


	public void setValueChangeDollars(double valueChangeDollars) {
		this.valueChangeDollars = valueChangeDollars;
	}


	public void setPriceChangePercent(double priceChangePercent) {
		this.priceChangePercent = priceChangePercent;
	}
}
