package com.sanjoyghosh.company.api;

public enum MarketIndexEnum {

	None(0),
	SnP500(1),
	DJIA(2),
	Nasdaq100(3);
	
	// Make sure the int's below match the index above.
	public static final int INDEX_NONE = 0;
	public static final int INDEX_SNP500 = 1;
	public static final int INDEX_DJIA = 2;
	public static final int INDEX_NASDAQ100 = 3;
	
	private int index;
	
	private MarketIndexEnum(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
}
