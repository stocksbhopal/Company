package com.sanjoyghosh.company.db.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class PortfolioItem {

	@Id()
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column
	private int companyId;
	@Column
	private LocalDate createDate;
	@Column
	private LocalDate validateDate;
	
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


	public int getCompanyId() {
		return companyId;
	}


	public void setCompanyId(int companyId) {
		this.companyId = companyId;
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


	@Override
	public String toString() {
		return "PortfolioItem [id=" + id + ", companyId=" + companyId + ", createDate=" + createDate + ", validateDate="
				+ validateDate + ", portfolio=" + portfolio + "]";
	}
}
