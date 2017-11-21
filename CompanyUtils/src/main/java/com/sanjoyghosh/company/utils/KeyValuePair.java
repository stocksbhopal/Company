package com.sanjoyghosh.company.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder({"k","v"})
public class KeyValuePair {

	private String	key;
	private String	value;
	
	
	public KeyValuePair(String key, String value) {
		this.key = key;
		this.value = value;
	}


	@JsonProperty("k")
	public String getKey() {
		return key;
	}


	@JsonProperty("v")
	public String getValue() {
		return value;
	}


	@JsonProperty("k")
	public void setKey(String key) {
		this.key = key;
	}


	@JsonProperty("v")
	public void setValue(String value) {
		this.value = value;
	}
}
