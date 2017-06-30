package com.sanjoyghosh.company.db.model;

import java.time.LocalDate;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
@Cacheable(false)
public class PortfolioItem {

	@Id()
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column
	private LocalDate createDate;
	@Column
	private LocalDate validateDate;
//	@Column
//	private double price;
//	@Column
//	private double priceChange;
//	@Column
//	private double priceChangePercent;
	@Column 
	private double quantity;	// This field has a SQL Default of 0.00.
//	@Column
//	private double valueChange;	// This is the product of the quantity and priceChange.
	
	@OneToOne
	@JoinColumn(name="companyId", referencedColumnName="id")
	private Company company;

	@ManyToOne
	@JoinColumn(name="portfolioId", referencedColumnName="id")
	private Portfolio portfolio;
	
	
	public PortfolioItem() {}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public Company getCompany() {
		return company;
	}


	public void setCompany(Company company) {
		this.company = company;
	}


	public LocalDate getCreateDate() {
		return createDate;
	}


	public void setCreateDate(LocalDate createDate) {
		this.createDate = createDate;
	}


	public LocalDate getValidateDate() {
		return validateDate;
	}


	public void setValidateDate(LocalDate validateDate) {
		this.validateDate = validateDate;
	}


	public Portfolio getPortfolio() {
		return portfolio;
	}


	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}


	public double getQuantity() {
		return quantity;
	}


	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}


	@Override
	public String toString() {
		return "PortfolioItem [id=" + id + ", company=" + company + ", createDate=" + createDate + ", validateDate="
				+ validateDate + ", quantity=" + quantity + ", portfolio=" + portfolio + "]";
	}

	/*
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


	public double getValueChange() {
		return valueChange;
	}


	public void setValueChange(double valueChange) {
		this.valueChange = valueChange;
	}
	*/
}
