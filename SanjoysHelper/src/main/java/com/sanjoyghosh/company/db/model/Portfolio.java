package com.sanjoyghosh.company.db.model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
@Cacheable(false)
public class Portfolio {

	@Id()
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column
	private String name;
	@Column
	private String alexaUserId;
	@Column
	private LocalDate createDate;
	@Column
	private LocalDate updateDate;
	
	@OneToMany(mappedBy="portfolio", cascade=CascadeType.ALL)
	private List<PortfolioItem> portfolioItemList;
	
	@Transient
	private transient Map<String, PortfolioItem> portfolioItemBySymbolMap = new HashMap<>();
	
	
	public Portfolio() {}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getAlexaUserId() {
		return alexaUserId;
	}


	public void setAlexaUserId(String alexaUserId) {
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


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setPortfolioItemList(List<PortfolioItem> portfolioItemList) {
		this.portfolioItemList = portfolioItemList;
		restorePortfolioItemBySymbolMap();
	}


	@Override
	public String toString() {
		return "Portfolio [id=" + id + ", name=" + name + ", alexaUserId=" + alexaUserId + ", createDate=" + createDate
				+ ", updateDate=" + updateDate + "]";
	}


	public PortfolioItem getPortfolioItemBySymbol(String symbol) {
		return portfolioItemBySymbolMap.get(symbol);
	}
	
	
	public void addPortfolioItem(PortfolioItem portfolioItem) {
		portfolioItemList.add(portfolioItem);
		portfolioItemBySymbolMap.put(portfolioItem.getCompany().getSymbol(), portfolioItem);
	}
	
	
	public void restorePortfolioItemBySymbolMap() {
		portfolioItemBySymbolMap.clear();
		for (PortfolioItem portfolioItem : portfolioItemList) {
			portfolioItemBySymbolMap.put(portfolioItem.getCompany().getSymbol(), portfolioItem);
		}		
	}


	public List<PortfolioItem> getPortfolioItemList() {
		return Collections.unmodifiableList(portfolioItemList);
	}
	
	
	public boolean isEmpty() {
		return portfolioItemList == null || portfolioItemList.size() == 0;
	}
}
