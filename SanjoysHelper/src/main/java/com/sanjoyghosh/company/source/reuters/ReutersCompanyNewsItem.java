package com.sanjoyghosh.company.source.reuters;

public class ReutersCompanyNewsItem {

	private String url;
	private String headline;
	private String summary;
	
	public ReutersCompanyNewsItem(String url, String headline, String summary) {
		this.url = url;
		this.headline = headline;
		this.summary = summary;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getHeadline() {
		return headline;
	}
	
	public String getSummary() {
		return summary;
	}
}
