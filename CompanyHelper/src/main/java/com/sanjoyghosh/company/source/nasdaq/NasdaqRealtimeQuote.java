package com.sanjoyghosh.company.source.nasdaq;

public class NasdaqRealtimeQuote {

	private String	symbol;
	private Double	price;
	private Double	priceChange;
	private Double	priceChangePercent;
	
	
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	
	
	public Double getPriceChange() {
		return priceChange;
	}
	public void setPriceChange(Double priceChange) {
		this.priceChange = priceChange;
	}
	
	
	public Double getPriceChangePercent() {
		return priceChangePercent;
	}
	public void setPriceChangePercent(Double priceChangePercent) {
		this.priceChangePercent = priceChangePercent;
	}
	
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
}
