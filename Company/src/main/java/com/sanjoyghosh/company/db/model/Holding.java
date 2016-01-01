package com.sanjoyghosh.company.db.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Holding {

	@Id()
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column
	private String symbol;
	@Column
	private int companyId;
	@Column
	private Double boughtPrice;
	@Column
	private Timestamp boughtDate;
	@Column
	private Double lastPrice;
	@Column
	private Timestamp lastDate;
	@Column
	private double quantity;
	@Column
	private String brokerage;
	@Column
	private String account;
	
	
	public Holding() {}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getCompanyId() {
		return companyId;
	}


	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}


	public Double getBoughtPrice() {
		return boughtPrice;
	}


	public void setBoughtPrice(Double boughtPrice) {
		this.boughtPrice = boughtPrice;
	}


	public Timestamp getBoughtDate() {
		return boughtDate;
	}


	public void setBoughtDate(Timestamp boughtDate) {
		this.boughtDate = boughtDate;
	}


	public Double getLastPrice() {
		return lastPrice;
	}


	public void setLastPrice(Double lastPrice) {
		this.lastPrice = lastPrice;
	}


	public Timestamp getLastDate() {
		return lastDate;
	}


	public void setLastDate(Timestamp lastDate) {
		this.lastDate = lastDate;
	}


	public double getQuantity() {
		return quantity;
	}


	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}


	public String getBrokerage() {
		return brokerage;
	}


	public void setBrokerage(String brokerage) {
		this.brokerage = brokerage;
	}


	public String getSymbol() {
		return symbol;
	}


	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}


	public String getAccount() {
		return account;
	}


	public void setAccount(String account) {
		this.account = account;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
		result = prime * result + ((brokerage == null) ? 0 : brokerage.hashCode());
		long temp;
		temp = Double.doubleToLongBits(quantity);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Holding other = (Holding) obj;
		if (account == null) {
			if (other.account != null)
				return false;
		} else if (!account.equals(other.account))
			return false;
		if (brokerage == null) {
			if (other.brokerage != null)
				return false;
		} else if (!brokerage.equals(other.brokerage))
			return false;
		if (Double.doubleToLongBits(quantity) != Double.doubleToLongBits(other.quantity))
			return false;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "Holding [id=" + id + ", symbol=" + symbol + ", companyId=" + companyId + ", boughtPrice=" + boughtPrice
				+ ", boughtDate=" + boughtDate + ", lastPrice=" + lastPrice + ", lastDate=" + lastDate + ", quantity="
				+ quantity + ", brokerage=" + brokerage + ", account=" + account + "]";
	}
}
