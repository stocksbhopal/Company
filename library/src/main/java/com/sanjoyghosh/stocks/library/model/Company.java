package com.sanjoyghosh.stocks.library.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Company {

	@Id()
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column
	private String symbol;
	@Column
	private String name;
	@Column(nullable=true)
	private int marketCap;
	@Column
	private int ipoYear;
	@Column
	private int industrySectorId;
	
	public Company() {}
	
	public Company(String symbol, String name, int ipoYear, int industrySectorId) {
		this.symbol = symbol;
		this.name = name;
		this.ipoYear = ipoYear;
		this.industrySectorId = industrySectorId;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		Company other = (Company) obj;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Company [id=" + id + ", symbol=" + symbol + ", name=" + name + ", marketCap=" + marketCap
				+ ", ipoYear=" + ipoYear + ", industrySectorId=" + industrySectorId + "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMarketCap() {
		return marketCap;
	}

	public void setMarketCap(int marketCap) {
		this.marketCap = marketCap;
	}

	public int getIpoYear() {
		return ipoYear;
	}

	public void setIpoYear(int ipoYear) {
		this.ipoYear = ipoYear;
	}

	public int getIndustrySectorId() {
		return industrySectorId;
	}

	public void setIndustrySectorId(int industrySectorId) {
		this.industrySectorId = industrySectorId;
	}

	public boolean isIdenticalNonNull(Company company) {
		if (!this.symbol.equals(company.symbol)) {
			return false;
		}
		if (!this.name.equals(company.name)) {
			return false;
		}
		if (this.ipoYear != company.ipoYear) {
			return false;
		}
		if (this.industrySectorId != company.industrySectorId) {
			return false;
		}
		return true;
	}
}
