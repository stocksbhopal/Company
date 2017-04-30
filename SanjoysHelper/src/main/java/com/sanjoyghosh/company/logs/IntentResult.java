package com.sanjoyghosh.company.logs;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazonaws.services.logs.model.InputLogEvent;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanjoyghosh.company.db.model.IntentResultLog;
import com.sanjoyghosh.company.utils.KeyValuePair;


@JsonPropertyOrder({"n","rt","rp","i","u","t"})
public class IntentResult {

    private static final Logger logger = Logger.getLogger(IntentResult.class.getName());

    
	private String				name;
	private int					result;
	private String				response;
	private List<KeyValuePair>	inputs;
	private String				alexaUserId;
	private long				eventTime;
	
	
	public IntentResult(String alexaUserId, String name, int result, 
		String response, List<KeyValuePair> inputs, Date eventTime) {

		this.alexaUserId = alexaUserId;
		this.name = name;
		this.result = result;
		this.response = response;
		this.inputs = inputs;
		this.eventTime = eventTime.getTime();
	}
	
	
	public InputLogEvent toInputLogEvent() {
		String message = null;
		try {
			message = new ObjectMapper().writeValueAsString(this);
		} 
		catch (JsonProcessingException e) {
			logger.log(Level.SEVERE, "Exception JSON serializing InputLogEvent", e);
		}
		
		InputLogEvent logEvent = new InputLogEvent();
		logEvent.setMessage(message);
		logEvent.setTimestamp(eventTime);
		return logEvent;
	}
	
	
	public IntentResultLog toIntentResultLog() {
		String inputsStr = null;
		if (inputs != null) {
			try {
				inputsStr = new ObjectMapper().writeValueAsString(inputs);
			} 
			catch (JsonProcessingException e) {
				logger.log(Level.SEVERE, "Exception JSON serializing Inputs", e);
			}
		}

		IntentResultLog intentResult = new IntentResultLog();
		intentResult.setAlexaUserId(alexaUserId);
		intentResult.setEventTime(new Timestamp(eventTime));
		intentResult.setInputs(inputsStr);
		intentResult.setName(name);
		intentResult.setResponse(response);
		intentResult.setResult(result);
		return intentResult;
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
	public long getEventTime() {
		return eventTime;
	}


	@JsonProperty("t")
	public void setEventTime(long eventTime) {
		this.eventTime = eventTime;
	}
}
