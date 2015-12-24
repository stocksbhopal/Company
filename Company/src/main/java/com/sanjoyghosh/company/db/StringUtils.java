package com.sanjoyghosh.company.db;

public class StringUtils {

	public static String stripFirstChar(String str) {
		if (str == null || str.length() < 2) {
			return null;
		}
		return str.substring(1, str.length());
	}
	
	
	public static Double toDoubleStringWithDollar(String str) {
		if (str == null || str.length() < 2) {
			return null;
		}
		if (str.equals("n/a")) {
			return null;
		}
		str = stripFirstChar(str);
		return Double.parseDouble(str);
	}


	public static String onlyLast4Characters(String str) {
		if (str == null || str.length() < 5) {
			return null;
		}
		str = str.substring(str.length() - 4, str.length());
		return str;
	}
	
	
	public static Float parseFloat(String floatStr) {
		if (floatStr == null) {
			return null;
		}
		floatStr = floatStr.trim();
		if (floatStr.equals("N/A")) {
			return null;
		}
		
		floatStr = floatStr.replaceAll(",", "");
		try {
			Float flt = Float.parseFloat(floatStr);
			return flt;
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	public static Long parseLongWithBM(String longStr) {
		if (longStr == null) {
			return null;
		}
		
		longStr = longStr.trim();
		if (longStr.endsWith("B")) {
			longStr = longStr.substring(0, longStr.length() - 1);
			Long longVal = StringUtils.parseFloat(longStr).longValue();
			longVal = longVal * 1000000000;
			return longVal;
		}
		if (longStr.endsWith("M")) {
			longStr = longStr.substring(0, longStr.length() - 1);
			Long longVal = StringUtils.parseFloat(longStr).longValue();
			longVal = longVal * 1000000;
			return longVal;
		}
		
		Float floatVal = StringUtils.parseFloat(longStr);
		return floatVal == null ? null : floatVal.longValue();
	}
}
