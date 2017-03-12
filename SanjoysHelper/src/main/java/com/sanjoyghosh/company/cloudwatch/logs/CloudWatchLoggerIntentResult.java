package com.sanjoyghosh.company.cloudwatch.logs;

import java.util.Date;
import java.util.List;

import com.amazonaws.services.logs.model.InputLogEvent;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanjoyghosh.company.utils.KeyValuePair;


@JsonPropertyOrder({"n","rt","rp","i","u","t"})
public class CloudWatchLoggerIntentResult {

	private String				name;
	private int					result;
	private String				response;
	private List<KeyValuePair>	inputs;
	private String				alexaUserId;
	private long				timestamp;
	
	
	public CloudWatchLoggerIntentResult(String alexaUserId, String name, int result, 
		String response, List<KeyValuePair> inputs, Date timestamp) {

		this.alexaUserId = alexaUserId;
		this.name = name;
		this.result = result;
		this.response = response;
		this.inputs = inputs;
		this.timestamp = timestamp.getTime();
	}
	
	
	public InputLogEvent toInputLogEvent() {
		String message = null;
		try {
			message = new ObjectMapper().writeValueAsString(this);
		} 
		catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		InputLogEvent logEvent = new InputLogEvent();
		logEvent.setMessage(message);
		logEvent.setTimestamp(timestamp);
		return logEvent;
	}


	@JsonProperty("u")
	public String getAlexaUserId() {
		return alexaUserId;
	}


	@JsonProperty("u")
	public void setAlexaUserId(String alexaUserId) {
		this.alexaUserId = alexaUserId;
	}


	@JsonProperty("n")
	public String getName() {
		return name;
	}


	@JsonProperty("n")
	public void setName(String name) {
		this.name = name;
	}


	@JsonProperty("rt")
	public int getResult() {
		return result;
	}


	@JsonProperty("rt")
	public void setResult(int result) {
		this.result = result;
	}


	@JsonProperty("rp")
	public String getResponse() {
		return response;
	}


	@JsonProperty("rp")
	public void setResponse(String response) {
		this.response = response;
	}


	@JsonProperty("i")
	public List<KeyValuePair> getInputs() {
		return inputs;
	}


	@JsonProperty("i")
	public void setInputs(List<KeyValuePair> inputs) {
		this.inputs = inputs;
	}


	@JsonProperty("t")
	public long getTimestamp() {
		return timestamp;
	}


	@JsonProperty("t")
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
