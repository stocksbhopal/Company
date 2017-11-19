package com.sanjoyghosh.company.alexaskill.source.reuters;

import com.sanjoyghosh.company.utils.StringUtils;

public class ReutersCompanyNewsItem {

	private String url;
	private String headline;
	private String summary;
	
	public ReutersCompanyNewsItem(String url, String headline, String summary) {
		this.url = url;
		this.headline = StringUtils.cleanForSSML(headline);
		this.summary = StringUtils.cleanForSSML(summary);
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
