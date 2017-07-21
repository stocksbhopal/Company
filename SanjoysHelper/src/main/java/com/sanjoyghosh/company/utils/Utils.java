package com.sanjoyghosh.company.utils;

public class Utils {

	public static int toInt(Double valueDouble) {
		if (valueDouble == null) {
			return 0;
		}
		return (int)(double)valueDouble;
	}

	public static int toInt(Integer valueInteger) {
		if (valueInteger == null) {
			return 0;
		}
		return valueInteger;
	}
	
	public static double toDouble(Double valueDouble) {
		if (valueDouble == null) {
			return 0.00D;
		}
		return valueDouble;
	}
	
	public static boolean toBoolean(Boolean valueBoolean) {
		if (valueBoolean == null) {
			return false;
		}
		return valueBoolean;
	}
}
