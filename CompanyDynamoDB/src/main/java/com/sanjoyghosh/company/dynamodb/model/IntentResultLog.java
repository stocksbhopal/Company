package com.sanjoyghosh.company.dynamodb.model;

import java.sql.Timestamp;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="IntentResultLog")
public class IntentResultLog {

	@DynamoDBHashKey(attributeName="EventTime")
	private Timestamp	eventTime;
	
	@DynamoDBRangeKey(attributeName="Name")
	private String		name;

	@DynamoDBAttribute(attributeName="Slots")
	private String 		slots;

	@DynamoDBAttribute(attributeName="Attributes")
	private String 		attributes;

	@DynamoDBAttribute(attributeName="ExecTimeMilliSecs")
	private int 			execTimeMilliSecs;

	@DynamoDBAttribute(attributeName="Result")
	private int 			result;

	@DynamoDBAttribute(attributeName="Response")
	private String 		response;

	@DynamoDBAttribute(attributeName="AlexaUserId")
	private String 		alexaUserId;

	@DynamoDBAttribute(attributeName="SessionId")
	private String 		sessionId;

	
	public Timestamp getEventTime() {
		return eventTime;
	}
	public void setEventTime(Timestamp eventTime) {
		this.eventTime = eventTime;
	}

	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	
	public String getSlots() {
		return slots;
	}
	public void setSlots(String slots) {
		this.slots = slots;
	}

	
	public String getAttributes() {
		return attributes;
	}
	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	
	public int getExecTimeMilliSecs() {
		return execTimeMilliSecs;
	}
	public void setExecTimeMilliSecs(int execTimeMilliSecs) {
		this.execTimeMilliSecs = execTimeMilliSecs;
	}

	
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}

	
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}

	
	public String getAlexaUserId() {
		return alexaUserId;
	}
	public void setAlexaUserId(String alexaUserId) {
		this.alexaUserId = alexaUserId;
	}

	
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
