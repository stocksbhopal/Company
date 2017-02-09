package com.sanjoyghosh.company.api;

public enum StartDateEnum {

	none(0),
	by(1),
	on(2);
	
	// Make sure the int's below match the index above.
	public static final int INDEX_NONE = 0;
	public static final int INDEX_BY= 1;
	public static final int INDEX_ON = 2;
	
	private int index;
	
	private StartDateEnum(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
}
