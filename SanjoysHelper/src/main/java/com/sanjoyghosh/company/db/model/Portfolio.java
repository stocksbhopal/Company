package com.sanjoyghosh.company.db.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Portfolio {

	@Id()
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column
	private int alexaUserId;
	@Column
	private LocalDate createDate;
	@Column
	private LocalDate updateDate;
	
	@OneToMany(mappedBy="portfolio", cascade=CascadeType.ALL)
	private List<PortfolioItem> portfolioItemList;
	
	
	public Portfolio() {}


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


	public LocalDate getCreateDate() {
		return createDate;
	}


	public void setCreateDate(LocalDate createDate) {
		this.createDate = createDate;
	}


	public LocalDate getUpdateDate() {
		return updateDate;
	}


	public void setUpdateDate(LocalDate updateDate) {
		this.updateDate = updateDate;
	}
}
