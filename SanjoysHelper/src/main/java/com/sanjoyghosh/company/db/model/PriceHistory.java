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
public class PriceHistory {

	@Id()
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column
	private Timestamp dateOfPrice;
	@Column
	private String symbol;
	@Column
	private int companyId;
	@Column
	private double openPrice;
	@Column
	private double closePrice;
	@Column
	private double lowPrice;
	@Column
	private double highPrice;
	@Column
	private int volume;
	
	
	public PriceHistory() {}


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

	
	@JsonFormat(pattern="yyyy-MM-dd")
	public Timestamp getDateOfPrice() {
		return dateOfPrice;
	}


	public void setDateOfPrice(Timestamp dateOfPrice) {
		this.dateOfPrice = dateOfPrice;
	}


	public String getSymbol() {
		return symbol;
	}


	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}


	public double getOpenPrice() {
		return openPrice;
	}


	public void setOpenPrice(double openPrice) {
		this.openPrice = openPrice;
	}


	public double getClosePrice() {
		return closePrice;
	}


	public void setClosePrice(double closePrice) {
		this.closePrice = closePrice;
	}


	public double getLowPrice() {
		return lowPrice;
	}


	public void setLowPrice(double lowPrice) {
		this.lowPrice = lowPrice;
	}


	public double getHighPrice() {
		return highPrice;
	}


	public void setHighPrice(double highPrice) {
		this.highPrice = highPrice;
	}


	public int getVolume() {
		return volume;
	}


	public void setVolume(int volume) {
		this.volume = volume;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(closePrice);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((dateOfPrice == null) ? 0 : dateOfPrice.hashCode());
		temp = Double.doubleToLongBits(highPrice);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lowPrice);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(openPrice);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		result = prime * result + volume;
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
		PriceHistory other = (PriceHistory) obj;
		if (Double.doubleToLongBits(closePrice) != Double.doubleToLongBits(other.closePrice))
			return false;
		if (dateOfPrice == null) {
			if (other.dateOfPrice != null)
				return false;
		} else if (!dateOfPrice.equals(other.dateOfPrice))
			return false;
		if (Double.doubleToLongBits(highPrice) != Double.doubleToLongBits(other.highPrice))
			return false;
		if (Double.doubleToLongBits(lowPrice) != Double.doubleToLongBits(other.lowPrice))
			return false;
		if (Double.doubleToLongBits(openPrice) != Double.doubleToLongBits(other.openPrice))
			return false;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		if (volume != other.volume)
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "PriceHistory [id=" + id + ", dateOfPrice=" + dateOfPrice + ", symbol=" + symbol + ", companyId="
				+ companyId + ", openPrice=" + openPrice + ", closePrice=" + closePrice + ", lowPrice=" + lowPrice
				+ ", highPrice=" + highPrice + ", volume=" + volume + "]";
	}
}
