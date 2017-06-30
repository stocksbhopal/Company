package com.sanjoyghosh.company.email;

import java.util.Date;
import java.util.List;

import com.sanjoyghosh.company.db.PortfolioItemData;

public class EarningsEmailModel {

	private List<DateListModel> earningsList;
	
	
	public class DateListModel {
		
		private Date earningsDate;
		private List<PortfolioItemData> itemList;
		
		
		public Date getEarningsDate() {
			return earningsDate;
		}
		public void setEarningsDate(Date earningsDate) {
			this.earningsDate = earningsDate;
		}
		
		
		public List<PortfolioItemData> getItemList() {
			return itemList;
		}
		public void setItemList(List<PortfolioItemData> itemList) {
			this.itemList = itemList;
		}
	}
	

	public List<DateListModel> getEarningsList() {
		return earningsList;
	}
	public void setEarningsList(List<DateListModel> earningsList) {
		this.earningsList = earningsList;
	}
}
