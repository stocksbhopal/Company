package com.sanjoyghosh.company.db.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
public class Holding {

	@Id()
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column
	private int companyId;
	@Column
	private Double boughtPrice;
	@Column
	private Timestamp boughtDate;
	@Column
	private Double cobPrice;
	@Column
	private Timestamp cobDate;
	@Column
	private Double quantity;
	@Column
	private String brokerage;
	@Column
	private String symbol;
	@Column
	private String account;
	@Column
	private Double value;
	@Column
	private Double gain;
	@Column
	private Double gainPercent;

	
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


	@JsonFormat(pattern="yyyy-MM-dd")
	public Timestamp getBoughtDate() {
		return boughtDate;
	}


	public void setBoughtDate(Timestamp boughtDate) {
		this.boughtDate = boughtDate;
	}


	public Double getCobPrice() {
		return cobPrice;
	}


	public void setCobPrice(Double cobPrice) {
		this.cobPrice = cobPrice;
	}


	@JsonFormat(pattern="yyyy-MM-dd")
	public Timestamp getCobDate() {
		return cobDate;
	}


	public void setCobDate(Timestamp cobDate) {
		this.cobDate = cobDate;
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


	public Double getValue() {
		return value;
	}


	public void setValue(Double value) {
		this.value = value;
	}


	public Double getGain() {
		return gain;
	}


	public void setGain(Double gain) {
		this.gain = gain;
	}


	public Double getGainPercent() {
		return gainPercent;
	}


	public void setGainPercent(Double gainPercent) {
		this.gainPercent = gainPercent;
	}


	@Override
	public String toString() {
		return "Holding [id=" + id + ", companyId=" + companyId + ", cobPrice=" + cobPrice + ", cobDate=" + cobDate
				+ ", quantity=" + quantity + ", brokerage=" + brokerage + ", symbol=" + symbol + ", account=" + account
				+ ", value=" + value + ", gain=" + gain + ", gainPercent=" + gainPercent + "]";
	}
}
