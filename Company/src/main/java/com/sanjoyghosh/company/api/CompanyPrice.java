package com.sanjoyghosh.company.api;

public class CompanyPrice {

	private String 		symbol;
	private String 		name;
	private double		price;
	private double		priceChange;
	private double		priceChangePercent;
	
	
	public CompanyPrice(String symbol, String name, double price, double priceChange, double priceChangePercent) {
		this.symbol = symbol;
		this.name = name;
		this.price = price;
		this.priceChange = priceChange;
		this.priceChangePercent = priceChangePercent;
	}
	
	
	public String getSymbol() {
		return symbol;
	}


	public String getName() {
		return name;
	}


	public double getPrice() {
		return price;
	}


	public double getPriceChange() {
		return priceChange;
	}


	public double getPriceChangePercent() {
		return priceChangePercent;
	}
}
