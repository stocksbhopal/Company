package com.sanjoyghosh.company.earnings.intent;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;


public class IntentResult {

    private static final Logger logger = Logger.getLogger(IntentResult.class.getName());
    

	private String						name;
	private AllSlotValues				slotValues;
	private Map<String, String> 		intentSlotMap;
	
	private boolean						isSsml;
	private String						speech;
	private Throwable					thrown;
	
	private Map<String, Set<String>>	symbolsByExceptionSet;
	private Set	<String>				symbolsWithNullQuotes;
	
	private int							execTimeMilliSecs;
	private int							result;
	private String						response;
	private Date						eventTime;
	private String						alexaUserId;
	private String						sessionId;

	
	// The sequencing of the lines below is VERY importante.
	public IntentResult(IntentRequest request, Session session) {
		this.name = request.getIntent().getName();
		this.slotValues = (AllSlotValues) session.getAttribute(InterfaceIntent.ATTR_ALL_SLOT_VALUES);
		this.slotValues = slotValues == null ? new AllSlotValues() : slotValues;
		
		this.intentSlotMap = new HashMap<>();
		IntentUtils.getSlotsFromIntent(request, this);
		IntentUtils.getCompanyOrSymbol(this);
		
		this.symbolsByExceptionSet = new HashMap<>();
		this.symbolsWithNullQuotes = new HashSet<>();
		
		this.alexaUserId = session.getUser().getUserId();
		this.sessionId = session.getSessionId();
		this.eventTime = new Date();
	}

	
	public void setSpeech(boolean isSsml, String speech) {
		this.isSsml = isSsml;
		this.speech = speech;
	}
	

	public String getName() {
		return name;
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


	public AllSlotValues getSlotValues() {
		return slotValues;
	}


	public boolean isSsml() {
		return isSsml;
	}


	public String getSpeech() {
		return speech;
	}


	public Throwable getThrown() {
		return thrown;
	}
	public void setThrown(Throwable thrown) {
		this.thrown = thrown;
	}


	public Map<String, String> getIntentSlotMap() {
		return intentSlotMap;
	}
}
