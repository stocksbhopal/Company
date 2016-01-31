package com.sanjoyghosh.company.utils;

import java.util.List;

public class StringUtils {

	public static String stripFirstChar(String str) {
		if (str == null || str.length() < 2) {
			return null;
		}
		return str.substring(1, str.length());
	}
	
	
	public static String[] prefixAndSuffixWithEmbedded(String str, List<String> embeddedList) {
		if (str == null) {
			return null;
		}
		
		for (String embedded : embeddedList) {
			int startPos = str.indexOf(embedded);
			if (startPos >= 0) {
				
				int endPos = startPos + embedded.length();
				if (str.length() > endPos) {
					String[] returnStrings = new String [3];
					String prefix = str.substring(0, startPos).trim();
					String suffix = str.substring(endPos).trim();
					returnStrings[0] = prefix;
					returnStrings[1] = embedded;
 					returnStrings[2] = suffix;
					return returnStrings;
				}
			}
		}
		return null;
	}

	
	public static String remainderWithInsidePrefix(String str, List<String> prefixList) {
		if (str == null) {
			return null;
		}
		
		for (String prefix : prefixList) {
			int pos = str.indexOf(prefix);
			if (pos >= 0) {
				pos += prefix.length();
				if (str.length() > pos) {
					str = str.substring(pos).trim();
					return str;
				}
			}
		}
		return null;
	}
	

	public static String remainderWithStartPrefix(String str, List<String> prefixList) {
		if (str == null) {
			return null;
		}
		
		for (String prefix : prefixList) {
			if (str.startsWith(prefix)) {
				int pos = prefix.length();
				if (str.length() > pos) {
					str = str.substring(pos).trim();
					return str;
				}
			}
		}
		return null;
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
	
	
	public static Double parseDouble(String doubleStr) {
		if (doubleStr == null) {
			return null;
		}
		doubleStr = doubleStr.trim();
		if (doubleStr.equals("N/A") || doubleStr.equals("NA")) {
			return null;
		}
		
		doubleStr = doubleStr.replaceAll(",", "");
		try {
			Double flt = Double.parseDouble(doubleStr);
			return flt;
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	public static Long parseLongWithBMK(String longStr) {
		if (longStr == null) {
			return null;
		}
		
		longStr = longStr.trim();
		if (longStr.endsWith("B")) {
			longStr = longStr.substring(0, longStr.length() - 1);
			Long longVal = (long) (StringUtils.parseDouble(longStr) * 1000000000);
			return longVal;
		}
		if (longStr.endsWith("M")) {
			longStr = longStr.substring(0, longStr.length() - 1);
			Long longVal = (long) (StringUtils.parseDouble(longStr) * 1000000D);
			return longVal;
		}
		if (longStr.endsWith("K")) {
			longStr = longStr.substring(0, longStr.length() - 1);
			Long longVal = (long) (StringUtils.parseDouble(longStr) * 1000D);
			return longVal;
		}
		
		Double doubleVal = StringUtils.parseDouble(longStr);
		return doubleVal == null ? null : doubleVal.longValue();
	}
	
	
	public static Integer parseInteger(String intStr) {
		if (intStr == null) {
			return null;
		}
		
		intStr = intStr.replaceAll(",", "");
		try {
			Integer integer = Integer.parseInt(intStr);
			return integer;
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

	
	public static Double[] parseDoubleRange(String doubleRangeStr) {
		if (doubleRangeStr == null) {
			return null;
		}
		
		String[] doubleStrings = doubleRangeStr.split("-");
		if (doubleStrings == null || doubleStrings.length != 2) {
			return null;
		}
		
		Double[] doubles = new Double [2];
		doubles[0] = StringUtils.parseDouble(doubleStrings[0]);
		doubles[1] = StringUtils.parseDouble(doubleStrings[1]);
		return doubles;
	}
}
