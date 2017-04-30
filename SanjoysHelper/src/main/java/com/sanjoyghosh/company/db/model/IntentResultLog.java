package com.sanjoyghosh.company.db.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Cacheable(false)
public class IntentResultLog implements Serializable {
	
	private static final long serialVersionUID = -4814018337581703331L;
	
	
	@Id()
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column
	private String name;
	@Column
	private int result;
	@Column
	private String response;
	@Column
	private String inputs;
	@Column
	private String alexaUserId;
	@Column
	private Timestamp eventTime;
	@Column
	private Integer execTimeMilliSecs;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	
	
	public String getInputs() {
		return inputs;
	}
	public void setInputs(String inputs) {
		this.inputs = inputs;
	}
	
	
	public String getAlexaUserId() {
		return alexaUserId;
	}
	public void setAlexaUserId(String alexaUserId) {
		this.alexaUserId = alexaUserId;
	}
	
	
	public Timestamp getEventTime() {
		return eventTime;
	}
	public void setEventTime(Timestamp eventTime) {
		this.eventTime = eventTime;
	}
	public Integer getExecTimeMilliSecs() {
		return execTimeMilliSecs;
	}
	public void setExecTimeMilliSecs(Integer execTimeMilliSecs) {
		this.execTimeMilliSecs = execTimeMilliSecs;
	};
}
