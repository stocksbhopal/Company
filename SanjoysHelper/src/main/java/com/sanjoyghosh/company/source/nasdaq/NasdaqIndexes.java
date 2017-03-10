package com.sanjoyghosh.company.source.nasdaq;

public class NasdaqIndexes {

	private int djia;	// We round up the index.  DJIA
	private double djiaChange;
	private double djiaChangePercent;
	
	private int ixic;	// We round up the index.  Nasdaq Composite
	private double ixicChange;
	private double ixicChangePercent;
	
	private int gspc;	// We round up the index.  S&P 500
	private double gspcChange;
	private double gspcChangePercent;
	
	
	public int getDjia() {
		return djia;
	}
	public void setDjia(int djia) {
		this.djia = djia;
	}
	public double getDjiaChange() {
		return djiaChange;
	}
	public void setDjiaChange(double djiaChange) {
		this.djiaChange = djiaChange;
	}
	public double getDjiaChangePercent() {
		return djiaChangePercent;
	}
	public void setDjiaChangePercent(double djiaChangePercent) {
		this.djiaChangePercent = djiaChangePercent;
	}
	
	
	public int getIxic() {
		return ixic;
	}
	public void setIxic(int ixic) {
		this.ixic = ixic;
	}
	public double getIxicChange() {
		return ixicChange;
	}
	public void setIxicChange(double ixicChange) {
		this.ixicChange = ixicChange;
	}
	public double getIxicChangePercent() {
		return ixicChangePercent;
	}
	public void setIxicChangePercent(double ixicChangePercent) {
		this.ixicChangePercent = ixicChangePercent;
	}
	
	
	public int getGspc() {
		return gspc;
	}
	public void setGspc(int gspc) {
		this.gspc = gspc;
	}
	public double getGspcChange() {
		return gspcChange;
	}
	public void setGspcChange(double gspcChange) {
		this.gspcChange = gspcChange;
	}
	public double getGspcChangePercent() {
		return gspcChangePercent;
	}
	public void setGspcChangePercent(double gspcChangePercent) {
		this.gspcChangePercent = gspcChangePercent;
	}
	
	
	@Override
	public String toString() {
		return "NasdaqIndexes [djia=" + djia + ", djiaChange=" + djiaChange + ", djiaChangePercent=" + djiaChangePercent
				+ ", ixic=" + ixic + ", ixicChange=" + ixicChange + ", ixicChangePercent=" + ixicChangePercent
				+ ", gspc=" + gspc + ", gspcChange=" + gspcChange + ", gspcChangePercent=" + gspcChangePercent + "]";
	}
}
