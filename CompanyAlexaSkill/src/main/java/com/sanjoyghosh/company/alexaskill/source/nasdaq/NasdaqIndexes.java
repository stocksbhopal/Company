package com.sanjoyghosh.company.alexaskill.source.nasdaq;

public class NasdaqIndexes {

	private NasdaqRealtimeQuote djiaQuote;
	private NasdaqRealtimeQuote ixicQuote;
	private NasdaqRealtimeQuote gspcQuote;
	
	
	public NasdaqRealtimeQuote getIxicQuote() {
		return ixicQuote;
	}
	public void setIxicQuote(NasdaqRealtimeQuote ixicQuote) {
		this.ixicQuote = ixicQuote;
	}
	
	
	public NasdaqRealtimeQuote getGspcQuote() {
		return gspcQuote;
	}
	public void setGspcQuote(NasdaqRealtimeQuote gspcQuote) {
		this.gspcQuote = gspcQuote;
	}
	
	
	public NasdaqRealtimeQuote getDjiaQuote() {
		return djiaQuote;
	}
	public void setDjiaQuote(NasdaqRealtimeQuote djiaQuote) {
		this.djiaQuote = djiaQuote;
	}
}
