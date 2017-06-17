package com.sanjoyghosh.company.earnings.intent;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanjoyghosh.company.db.model.IntentResultLog;
import com.sanjoyghosh.company.utils.KeyValuePair;


public class IntentResult {

    private static final Logger logger = Logger.getLogger(IntentResult.class.getName());
    

	private String						name;
	private List<KeyValuePair>			slots;
	private List<KeyValuePair>			attributes;
	private Map<String, Set<String>>	symbolsByExceptionSet;
	private Set	<String>				symbolsWithNullQuotes;
	private int							execTimeMilliSecs;
	private int							result;
	private String						response;
	private Date						eventTime;
	private String						alexaUserId;
	private String						sessionId;

	
	public IntentResult(IntentRequest request, Session session) {
		this.name = request.getIntent().getName();
		this.slots = IntentUtils.getSlotsFromIntent(request);
		this.attributes = IntentUtils.getAttributesFromSession(session);
		this.symbolsByExceptionSet = new HashMap<>();
		this.symbolsWithNullQuotes = new HashSet<>();
		this.alexaUserId = session.getUser().getUserId();
		this.sessionId = session.getSessionId();
		this.eventTime = new Date();
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


	public Date getEventTime() {
		return eventTime;
	}


	public String getAlexaUserId() {
		return alexaUserId;
	}


	public String getSessionId() {
		return sessionId;
	}

	
	private String listToJson(List<KeyValuePair> keyValueList) {
		Map<String, String> map = new HashMap<>();
		for (KeyValuePair pair : keyValueList) {
			map.put(pair.getKey().substring(0, 1), pair.getValue());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(map);
			return json;
		} 
		catch (JsonProcessingException e) {
			logger.log(Level.SEVERE, "Cannot serialize JSON", e);
			return null;
		}
	}
	
	
	public void addNullQuoteSymbol(String symbol) {
		symbolsWithNullQuotes.add(symbol);
	}
	
	
	public void addSymbolWithException(String symbol, Exception exception) {
		String key = exception.getClass().getName() + ":" + exception.getMessage();
		Set<String> symbols = symbolsByExceptionSet.get(key);
		if (symbols == null) {
			symbols = new HashSet<>();
			symbolsByExceptionSet.put(key, symbols);
		}
		symbols.add(symbol);
	}


	public IntentResultLog toIntentResultLog() {
		IntentResultLog intentResultlog = new IntentResultLog();
		
		intentResultlog.setAlexaUserId(alexaUserId);
		intentResultlog.setAttributes(listToJson(attributes));
		intentResultlog.setEventTime(new Timestamp(eventTime.getTime()));
		intentResultlog.setExecTimeMilliSecs(execTimeMilliSecs);
		intentResultlog.setName(name);
		intentResultlog.setResponse(response);
		intentResultlog.setResponse(response);
		intentResultlog.setSessionId(sessionId);
		intentResultlog.setSlots(listToJson(slots));
		
		return intentResultlog;
	}
}
