package com.sanjoyghosh.company.db.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
public class DividendHistory {

	@Id()
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column
	private Timestamp dateOfDividend;
	@Column
	private String symbol;
	@Column
	private int companyId;
	@Column
	private double dividend;
	
	
	public DividendHistory() {}


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


	public String getSymbol() {
		return symbol;
	}


	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}


	@JsonFormat(pattern="yyyy-MM-dd")
	public Timestamp getDateOfDividend() {
		return dateOfDividend;
	}


	public void setDateOfDividend(Timestamp dateOfDividend) {
		this.dateOfDividend = dateOfDividend;
	}


	public double getDividend() {
		return dividend;
	}


	public void setDividend(double dividend) {
		this.dividend = dividend;
	}


	@Override
	public String toString() {
		return "DividendHistory [id=" + id + ", dateOfDividend=" + dateOfDividend + ", symbol=" + symbol
				+ ", companyId=" + companyId + ", dividend=" + dividend + "]";
	}
}
