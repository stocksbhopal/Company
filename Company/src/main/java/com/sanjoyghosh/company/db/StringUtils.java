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
}
