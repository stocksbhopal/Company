package com.sanjoyghosh.company.earnings.intent;

import java.util.List;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.sanjoyghosh.company.db.model.IntentResultLog;
import com.sanjoyghosh.company.utils.KeyValuePair;


public class IntentResult {

	private String				name;
	private List<KeyValuePair>	slots;
	private List<KeyValuePair>	attributes;
	private int					execTimeMilliSecs;
	private int					result;
	private String				response;
	private long				eventTime;
	private String				alexaUserId;
	private String				sessionId;

	
	public IntentResult(IntentRequest request, Session session) {
		this.name = request.getIntent().getName();
		this.slots = IntentUtils.getSlotsFromIntent(request);
		this.attributes = IntentUtils.getAttributesFromSession(session);
		this.alexaUserId = session.getUser().getUserId();
		this.sessionId = session.getSessionId();
	}


	public String getName() {
		return name;
	}


	public List<KeyValuePair> getSlots() {
		return slots;
	}


	public List<KeyValuePair> getAttributes() {
		return attributes;
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


	public long getEventTime() {
		return eventTime;
	}


	public String getAlexaUserId() {
		return alexaUserId;
	}


	public String getSessionId() {
		return sessionId;
	}


	public IntentResultLog toIntentResultLog() {
		// TODO Auto-generated method stub
		return null;
	}
}
