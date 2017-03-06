package com.sanjoyghosh.company.cloudwatch.logs;

import java.util.Date;
import java.util.List;

import com.amazonaws.services.logs.model.InputLogEvent;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanjoyghosh.company.utils.KeyValuePair;


@JsonPropertyOrder({"uid","in","irt","irp","irs","ts"})
public class CloudWatchLoggerIntentResult {

	private String				alexaUserId;
	private String				intentName;
	private int					intentResult;
	private String				intentResponse;
	private List<KeyValuePair>	intentSlots;
	private long				timestamp;
	
	
	public CloudWatchLoggerIntentResult(String alexaUserId, String intentName, int intentResult, 
		String intentResponse, List<KeyValuePair> intentSlots, Date timestamp) {

		this.alexaUserId = alexaUserId;
		this.intentName = intentName;
		this.intentResult = intentResult;
		this.intentResponse = intentResponse;
		this.intentSlots = intentSlots;
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


	@JsonProperty("uid")
	public String getAlexaUserId() {
		return alexaUserId;
	}


	@JsonProperty("uid")
	public void setAlexaUserId(String alexaUserId) {
		this.alexaUserId = alexaUserId;
	}


	@JsonProperty("in")
	public String getIntentName() {
		return intentName;
	}


	@JsonProperty("in")
	public void setIntentName(String intentName) {
		this.intentName = intentName;
	}


	@JsonProperty("irt")
	public int getIntentResult() {
		return intentResult;
	}


	@JsonProperty("irt")
	public void setIntentResult(int intentResult) {
		this.intentResult = intentResult;
	}


	@JsonProperty("irp")
	public String getIntentResponse() {
		return intentResponse;
	}


	@JsonProperty("irp")
	public void setIntentResponse(String intentResponse) {
		this.intentResponse = intentResponse;
	}


	@JsonProperty("irs")
	public List<KeyValuePair> getIntentSlots() {
		return intentSlots;
	}


	@JsonProperty("irs")
	public void setIntentSlots(List<KeyValuePair> intentSlots) {
		this.intentSlots = intentSlots;
	}


	@JsonProperty("ts")
	public long getTimestamp() {
		return timestamp;
	}


	@JsonProperty("ts")
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
