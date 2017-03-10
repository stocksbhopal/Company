package com.sanjoyghosh.company.db.model;

import java.sql.Timestamp;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Cacheable(false)
public class StockSplitHistory {

	@Id()
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column
	private Timestamp dateOfSplit;
	@Column
	private String symbol;
	@Column
	private int companyId;
	@Column
	private double splitRatio;
	
	
	public StockSplitHistory() {}


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
	public Timestamp getDateOfSplit() {
		return dateOfSplit;
	}


	public void setDateOfSplit(Timestamp dateOfSplit) {
		this.dateOfSplit = dateOfSplit;
	}


	public double getSplitRatio() {
		return splitRatio;
	}


	public void setSplitRatio(double splitRatio) {
		this.splitRatio = splitRatio;
	}


	@Override
	public String toString() {
		return "StockSplitHistory [id=" + id + ", dateOfSplit=" + dateOfSplit + ", symbol=" + symbol + ", companyId="
				+ companyId + ", splitRatio=" + splitRatio + "]";
	}
}
