package com.sanjoyghosh.company.db.model;

import java.time.LocalDateTime;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Cacheable(false)
public class Price {

	@Id()
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column
	private int companyId;
	@Column
	private LocalDateTime dateTime;
	@Column
	private double price;
	@Column
	private double priceChange;
	@Column
	private double priceChangePercent;
	
	
	public Price() {}


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
	
	
	public LocalDateTime getDateTime() {
		return dateTime;
	}


	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}


	public double getPrice() {
		return price;
	}


	public void setPrice(double price) {
		this.price = price;
	}


	public double getPriceChange() {
		return priceChange;
	}


	public void setPriceChange(double priceChange) {
		this.priceChange = priceChange;
	}


	public double getPriceChangePercent() {
		return priceChangePercent;
	}


	public void setPriceChangePercent(double priceChangePercent) {
		this.priceChangePercent = priceChangePercent;
	}


	@Override
	public String toString() {
		return "Price [id=" + id + ", companyId=" + companyId + ", dateTime=" + dateTime + ", price=" + price
				+ ", priceChange=" + priceChange + ", priceChangePercent=" + priceChangePercent + "]";
	}
}
