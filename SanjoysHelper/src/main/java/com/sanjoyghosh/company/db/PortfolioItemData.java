package com.sanjoyghosh.company.db;

import java.time.LocalDate;

public class PortfolioItemData {

	private String symbol;
	private String name;
	private String speechName;
	private Double price;
	private Double priceChange;
	private double priceChangePercent;
	private double quantity;
	private LocalDate earningsDate;
	private String beforeMarketOrAfterMarket;
	private double valueChangeDollars;

	
	public PortfolioItemData(String symbol, String name, String speechName, 
		Double price, Double priceChange, Double priceChangePercent, Double quantity) {
		
		this.symbol = symbol;
		this.name = name;
		this.speechName = speechName;
		this.price = price;
		this.priceChange = priceChange;
		this.priceChangePercent = priceChangePercent;
		this.quantity = quantity;
		this.valueChangeDollars = priceChange * quantity;
	}


	public PortfolioItemData(String symbol, String name, String speechName, 
		Double price, Double priceChange, Double priceChangePercent, Double quantity,
		LocalDate earningsDate, String beforeMarketOrAfterMarket) {
		
		this.symbol = symbol;
		this.name = name;
		this.speechName = speechName;
		this.price = price;
		this.priceChange = priceChange;
		this.priceChangePercent = priceChangePercent;
		this.quantity = quantity;
		this.valueChangeDollars = priceChange * quantity;
		this.earningsDate = earningsDate;
		this.beforeMarketOrAfterMarket = beforeMarketOrAfterMarket;
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


	public LocalDate getEarningsDate() {
		return earningsDate;
	}


	public String getBeforeMarketOrAfterMarket() {
		return beforeMarketOrAfterMarket;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	@Override
	public String toString() {
		return "PortfolioItemData [symbol=" + symbol + ", name=" + name + ", speechName=" + speechName + ", price="
				+ price + ", priceChange=" + priceChange + ", quantity=" + quantity + ", earningsDate=" + earningsDate
				+ ", beforeMarketOrAfterMarket=" + beforeMarketOrAfterMarket + ", valueChangeDollars="
				+ valueChangeDollars + ", priceChangePercent=" + priceChangePercent + "]";
	}
}
