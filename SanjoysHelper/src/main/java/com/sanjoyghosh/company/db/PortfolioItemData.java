package com.sanjoyghosh.company.db;

public class PortfolioItemData {

	private String speechName;
	private double quantity;

	
	public PortfolioItemData(String speechName, Double quantity) {
		this.speechName = speechName;
		this.quantity = quantity;
	}


	public String getSpeechName() {
		return speechName;
	}


	public double getQuantity() {
		return quantity;
	}
}
