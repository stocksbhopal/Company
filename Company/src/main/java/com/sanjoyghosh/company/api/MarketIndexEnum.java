package com.sanjoyghosh.company.api;

import java.util.HashMap;
import java.util.Map;

public enum MarketIndexEnum {

	None(0, "none"),
	SnP500(1, "s&p"),
	DJIA(2, "dow jones"),
	Nasdaq100(3, "nasdaq");
	
	// Make sure the int's below match the index above.
	public static final int INDEX_NONE = 0;
	public static final int INDEX_SNP500 = 1;
	public static final int INDEX_DJIA = 2;
	public static final int INDEX_NASDAQ100 = 3;
	
	private static final Map<String, MarketIndexEnum> marketIndexByAsrNameMap = new HashMap<>();
	static {
		marketIndexByAsrNameMap.put(None.asrName, None);
		marketIndexByAsrNameMap.put(SnP500.asrName, SnP500);
		marketIndexByAsrNameMap.put(DJIA.asrName, DJIA);
		marketIndexByAsrNameMap.put(Nasdaq100.asrName, Nasdaq100);
	}
	
	
	private int index;
	private String asrName;
	
	
	private MarketIndexEnum(int index, String asrName) {
		this.index = index;
		this.asrName = asrName;
	}

	
	public int getIndex() {
		return index;
	}
	
	
	public static MarketIndexEnum toMarketIndexEnum(String asrName) {
		return marketIndexByAsrNameMap.get(asrName.toLowerCase());
	}
}
