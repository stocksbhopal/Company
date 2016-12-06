package com.sanjoyghosh.company.db.model;

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
	@Column
	private Double analystOpinion;
	@Column
	private Integer ipoYear;
	@Column
	private String sector;
	@Column
	private String industry;
	@Column
	private String exchange;
	@Column 
	private Long marketCap;
	@Column
	private String marketCapBM;
	@Column
	private String isSnP500;
	@Column
	private String isDJIA;
	@Column
	private String isNasdaq100;
	
	
	public Company() {}


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


	public Integer getIpoYear() {
		return ipoYear;
	}


	public void setIpoYear(Integer ipoYear) {
		this.ipoYear = ipoYear;
	}


	public String getSector() {
		return sector;
	}


	public void setSector(String sector) {
		this.sector = sector;
	}


	public String getIndustry() {
		return industry;
	}


	public void setIndustry(String industry) {
		this.industry = industry;
	}


	public String getExchange() {
		return exchange;
	}


	public void setExchange(String exchange) {
		this.exchange = exchange;
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


	public Long getMarketCap() {
		return marketCap;
	}


	public void setMarketCap(Long marketCap) {
		this.marketCap = marketCap;
		
		if (marketCap > 1000000000L) {
			int marketCapBillions = (int) (marketCap / 1000000000L);
			marketCapBM = String.valueOf(marketCapBillions) + " billion";
		}
		else if (marketCap > 1000000) {
			int marketCapMillions = (int) (marketCap / 1000000);
			marketCapBM = String.valueOf(marketCapMillions) + " million";
		}
		else {
			marketCapBM = String.valueOf(marketCap);
		}
	}


	public String getMarketCapBM() {
		return marketCapBM;
	}


	public String getIsSnP500() {
		return isSnP500;
	}


	public void setIsSnP500(String isSnP500) {
		this.isSnP500 = isSnP500;
	}


	public String getIsDJIA() {
		return isDJIA;
	}


	public void setIsDJIA(String isDJIA) {
		this.isDJIA = isDJIA;
	}


	public String getIsNasdaq100() {
		return isNasdaq100;
	}


	public void setIsNasdaq100(String isNasdaq100) {
		this.isNasdaq100 = isNasdaq100;
	}


	public Double getAnalystOpinion() {
		return analystOpinion;
	}


	public void setAnalystOpinion(Double analystOpinion) {
		this.analystOpinion = analystOpinion;
	}


	@Override
	public String toString() {
		return "Company [id=" + id + ", symbol=" + symbol + ", name=" + name + ", analystOpinion=" + analystOpinion
				+ ", ipoYear=" + ipoYear + ", sector=" + sector + ", industry=" + industry + ", exchange=" + exchange
				+ ", marketCap=" + marketCap + ", marketCapBM=" + marketCapBM + ", isSnP500=" + isSnP500 + ", isDJIA="
				+ isDJIA + ", isNasdaq100=" + isNasdaq100 + "]";
	}	
}
