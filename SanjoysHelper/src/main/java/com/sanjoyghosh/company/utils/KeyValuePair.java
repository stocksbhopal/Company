package com.sanjoyghosh.company.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder({"K","V"})
public class KeyValuePair {

	private String	key;
	private String	value;
	
	
	public KeyValuePair(String key, String value) {
		this.key = key;
		this.value = value;
	}


	@JsonProperty("K")
	public String getKey() {
		return key;
	}


	@JsonProperty("V")
	public String getValue() {
		return value;
	}


	@JsonProperty("K")
	public void setKey(String key) {
		this.key = key;
	}


	@JsonProperty("V")
	public void setValue(String value) {
		this.value = value;
	}
}
