package com.sanjoyghosh.company.db.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
public class EarningsDate {

	@Id()
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column
	private String symbol;
	@Column
	private String name;
	@Column
	private Integer companyId;
	@Column
	private Timestamp earningsDate;
	@Column
	private String beforeMarketOrAfterMarket;
	@Column
	private Double analystOpinion;
	@Column
	private Long marketCap;
	@Column
	private String marketCapBM;
	@Column
	private String jpmOpinion;
	@Column
	private String jpmAnalyst;
	
	
	public EarningsDate() {}


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


	public Integer getCompanyId() {
		return companyId;
	}


	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}


	@JsonFormat(pattern="yyyy-MM-dd")
	public Timestamp getEarningsDate() {
		return earningsDate;
	}


	public void setEarningsDate(Timestamp earningsDate) {
		this.earningsDate = earningsDate;
	}


	public String getBeforeMarketOrAfterMarket() {
		return beforeMarketOrAfterMarket;
	}


	public void setBeforeMarketOrAfterMarket(String beforeMarketOrAfterMarket) {
		this.beforeMarketOrAfterMarket = beforeMarketOrAfterMarket;
	}


	public Double getAnalystOpinion() {
		return analystOpinion;
	}


	public void setAnalystOpinion(Double analystOpinion) {
		this.analystOpinion = analystOpinion;
	}


	public String getMarketCapBM() {
		return getMarketCapBM();
	}


	public void setMarketCapBM(String marketCapBM) {
		this.marketCapBM = marketCapBM;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((earningsDate == null) ? 0 : earningsDate.hashCode());
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
		EarningsDate other = (EarningsDate) obj;
		if (earningsDate == null) {
			if (other.earningsDate != null)
				return false;
		} else if (!earningsDate.equals(other.earningsDate))
			return false;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		return true;
	}


	public String getJpmOpinion() {
		return jpmOpinion;
	}


	public void setJpmOpinion(String jpmOpinion) {
		this.jpmOpinion = jpmOpinion;
	}


	public String getJpmAnalyst() {
		return jpmAnalyst;
	}


	public void setJpmAnalyst(String jpmAnalyst) {
		this.jpmAnalyst = jpmAnalyst;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Long getMarketCap() {
		return marketCap;
	}


	public void setMarketCap(Long marketCap) {
		this.marketCap = marketCap;
	}


	@Override
	public String toString() {
		return "EarningsDate [id=" + id + ", symbol=" + symbol + ", name=" + name + ", companyId=" + companyId
				+ ", earningsDate=" + earningsDate + ", beforeMarketOrAfterMarket=" + beforeMarketOrAfterMarket
				+ ", analystOpinion=" + analystOpinion + ", marketCap=" + marketCap + ", marketCapBM=" + marketCapBM
				+ ", jpmOpinion=" + jpmOpinion + ", jpmAnalyst=" + jpmAnalyst + "]";
	}
}
