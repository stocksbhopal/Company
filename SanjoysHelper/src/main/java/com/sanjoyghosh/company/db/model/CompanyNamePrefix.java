package com.sanjoyghosh.company.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class CompanyNamePrefix {

	@Id()
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column
	private String companyNamePrefix;
	@Column
	private boolean manuallyAdded;
	
	@ManyToOne
	@JoinColumn(name="symbol", referencedColumnName="symbol")
	private Company company;
	
	
	public CompanyNamePrefix() {}
	
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getCompanyNamePrefix() {
		return companyNamePrefix;
	}


	public void setCompanyNamePrefix(String companyNamePrefix) {
		this.companyNamePrefix = companyNamePrefix;
	}


	public Company getCompany() {
		return company;
	}


	public boolean isManuallyAdded() {
		return manuallyAdded;
	}


	public void setManuallyAdded(boolean manuallyAdded) {
		this.manuallyAdded = manuallyAdded;
	}
}
