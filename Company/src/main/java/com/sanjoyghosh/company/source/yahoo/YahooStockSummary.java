package com.sanjoyghosh.company.source.yahoo;

public class YahooStockSummary {

	private Double price;
	private Double previousClose;
	private Double open;
	private Double oneYearTarget;
	private Double dayRangeLow;
	private Double dayRangeHigh;
	private Double fiftyTwoWeekRangeLow;
	private Double fiftyTwoWeekRangeHigh;
	private Integer volume;
	private Integer threeMonthAverageVolume;
	private Long marketCap;
	private String marketCapBM;
	
	
	public YahooStockSummary() {}
	
	
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	
	
	public Double getPreviousClose() {
		return previousClose;
	}
	public void setPreviousClose(Double previousClose) {
		this.previousClose = previousClose;
	}
	
	
	public Double getOpen() {
		return open;
	}
	public void setOpen(Double open) {
		this.open = open;
	}
	
	
	public Double getOneYearTarget() {
		return oneYearTarget;
	}
	public void setOneYearTarget(Double oneYearTarget) {
		this.oneYearTarget = oneYearTarget;
	}
	
	
	public Double getDayRangeLow() {
		return dayRangeLow;
	}
	public void setDayRangeLow(Double dayRangeLow) {
		this.dayRangeLow = dayRangeLow;
	}
	
	
	public Double getDayRangeHigh() {
		return dayRangeHigh;
	}
	public void setDayRangeHigh(Double dayRangeHigh) {
		this.dayRangeHigh = dayRangeHigh;
	}
	
	
	public Double getFiftyTwoWeekRangeLow() {
		return fiftyTwoWeekRangeLow;
	}
	public void setFiftyTwoWeekRangeLow(Double fiftyTwoWeekRangeLow) {
		this.fiftyTwoWeekRangeLow = fiftyTwoWeekRangeLow;
	}
	
	
	public Double getFiftyTwoWeekRangeHigh() {
		return fiftyTwoWeekRangeHigh;
	}
	public void setFiftyTwoWeekRangeHigh(Double fiftyTwoWeekRangeHigh) {
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


	public String getMarketCapBM() {
		return marketCapBM;
	}


	public void setMarketCapBM(String marketCapBM) {
		this.marketCapBM = marketCapBM;
	}
}
