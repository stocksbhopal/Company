package com.sanjoyghosh.company.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class MyStocks {

	@Id()
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column
	private int alexaUserId;
	@Column
	private int companyId;
	
	
	public MyStocks() {}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getAlexaUserId() {
		return alexaUserId;
	}


	public void setAlexaUserId(int alexaUserId) {
		this.alexaUserId = alexaUserId;
	}


	public int getCompanyId() {
		return companyId;
	}


	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}


	@Override
	public String toString() {
		return "MyStocks [id=" + id + ", alexaUserId=" + alexaUserId + ", companyId=" + companyId + "]";
	}
}
