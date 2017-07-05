package com.sanjoyghosh.company.email;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.sanjoyghosh.company.db.PortfolioItemData;

public class EarningsEmailModel {

	private List<DateListModel> earningsList = new ArrayList<>();
	
	
	public class DateListModel {
		
		private LocalDate earningsDate;
		private List<PortfolioItemData> itemList = new ArrayList<>();
		
		
		public LocalDate getEarningsDate() {
			return earningsDate;
		}
		public void setEarningsDate(LocalDate earningsDate) {
			this.earningsDate = earningsDate;
		}
		
		
		public List<PortfolioItemData> getItemList() {
			return itemList;
		}
		public void addPortfolioItemData(PortfolioItemData portfolioItemData) {
			this.itemList.add(portfolioItemData);
		}
	}
	

	public List<DateListModel> getEarningsList() {
		return earningsList;
	}
	public void addDateListModel(DateListModel dateListModel) {
		this.earningsList.add(dateListModel);
	}
}
