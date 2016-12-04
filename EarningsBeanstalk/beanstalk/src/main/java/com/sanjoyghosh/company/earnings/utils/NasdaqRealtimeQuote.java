package com.sanjoyghosh.company.earnings.utils;

public class NasdaqRealtimeQuote {

	private Double	price;
	private Double	priceChange;
	private Double	priceChangePercent;
	private Double	previousClose;
	private Integer	volumeToday;
	private Double	lowToday;
	private Double	highToday;
	private Double	low52Week;
	private Double	high52Week;
	
	
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	
	
	public Double getPriceChange() {
		return priceChange;
	}
	public void setPriceChange(Double priceChange) {
		this.priceChange = priceChange;
	}
	
	
	public Double getPriceChangePercent() {
		return priceChangePercent;
	}
	public void setPriceChangePercent(Double priceChangePercent) {
		this.priceChangePercent = priceChangePercent;
	}
	
	
	public Double getPreviousClose() {
		return previousClose;
	}
	public void setPreviousClose(Double previousClose) {
		this.previousClose = previousClose;
	}
	
	
	public Integer getVolumeToday() {
		return volumeToday;
	}
	public void setVolumeToday(Integer volumeToday) {
		this.volumeToday = volumeToday;
	}
	
	
	public Double getLowToday() {
		return lowToday;
	}
	public void setLowToday(Double lowToday) {
		this.lowToday = lowToday;
	}
	
	
	public Double getHighToday() {
		return highToday;
	}
	public void setHighToday(Double highToday) {
		this.highToday = highToday;
	}
	
	
	public Double getLow52Week() {
		return low52Week;
	}
	public void setLow52Week(Double low52Week) {
		this.low52Week = low52Week;
	}
	
	
	public Double getHigh52Week() {
		return high52Week;
	}
	public void setHigh52Week(Double high52Week) {
		this.high52Week = high52Week;
	}
	
	
	@Override
	public String toString() {
		return "NasdaqRealtimeQuote [price=" + price + ", priceChange=" + priceChange + ", priceChangePercent="
				+ priceChangePercent + ", previousClose=" + previousClose + ", volumeToday=" + volumeToday
				+ ", lowToday=" + lowToday + ", highToday=" + highToday + ", low52Week=" + low52Week + ", high52Week="
				+ high52Week + "]";
	}
}
