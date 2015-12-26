package com.sanjoyghosh.company.yahoo;

public class YahooStockSummary {

	private Float price;
	private Float previousClose;
	private Float open;
	private Float oneYearTarget;
	private Float dayRangeLow;
	private Float dayRangeHigh;
	private Float fiftyTwoWeekRangeLow;
	private Float fiftyTwoWeekRangeHigh;
	private Integer volume;
	private Integer threeMonthAverageVolume;
	private Long marketCap;
	
	
	public YahooStockSummary() {}
	
	
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	
	
	public Float getPreviousClose() {
		return previousClose;
	}
	public void setPreviousClose(Float previousClose) {
		this.previousClose = previousClose;
	}
	
	
	public Float getOpen() {
		return open;
	}
	public void setOpen(Float open) {
		this.open = open;
	}
	
	
	public Float getOneYearTarget() {
		return oneYearTarget;
	}
	public void setOneYearTarget(Float oneYearTarget) {
		this.oneYearTarget = oneYearTarget;
	}
	
	
	public Float getDayRangeLow() {
		return dayRangeLow;
	}
	public void setDayRangeLow(Float dayRangeLow) {
		this.dayRangeLow = dayRangeLow;
	}
	
	
	public Float getDayRangeHigh() {
		return dayRangeHigh;
	}
	public void setDayRangeHigh(Float dayRangeHigh) {
		this.dayRangeHigh = dayRangeHigh;
	}
	
	
	public Float getFiftyTwoWeekRangeLow() {
		return fiftyTwoWeekRangeLow;
	}
	public void setFiftyTwoWeekRangeLow(Float fiftyTwoWeekRangeLow) {
		this.fiftyTwoWeekRangeLow = fiftyTwoWeekRangeLow;
	}
	
	
	public Float getFiftyTwoWeekRangeHigh() {
		return fiftyTwoWeekRangeHigh;
	}
	public void setFiftyTwoWeekRangeHigh(Float fiftyTwoWeekRangeHigh) {
		this.fiftyTwoWeekRangeHigh = fiftyTwoWeekRangeHigh;
	}
	
	
	public Integer getVolume() {
		return volume;
	}
	public void setVolume(Integer volume) {
		this.volume = volume;
	}
	
	
	public Integer getThreeMonthAverageVolume() {
		return threeMonthAverageVolume;
	}
	public void setThreeMonthAverageVolume(Integer threeMonthAverageVolume) {
		this.threeMonthAverageVolume = threeMonthAverageVolume;
	}
	
	
	public Long getMarketCap() {
		return marketCap;
	}
	public void setMarketCap(Long marketCap) {
		this.marketCap = marketCap;
	}


	@Override
	public String toString() {
		return "YahooStockSummary [price=" + price + ", previousClose=" + previousClose + ", open=" + open
				+ ", oneYearTarget=" + oneYearTarget + ", dayRangeLow=" + dayRangeLow + ", dayRangeHigh=" + dayRangeHigh
				+ ", fiftyTwoWeekRangeLow=" + fiftyTwoWeekRangeLow + ", fiftyTwoWeekRangeHigh=" + fiftyTwoWeekRangeHigh
				+ ", volume=" + volume + ", threeMonthAverageVolume=" + threeMonthAverageVolume + ", marketCap="
				+ marketCap + "]";
	}
}
