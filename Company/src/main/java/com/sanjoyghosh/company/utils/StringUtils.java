package com.sanjoyghosh.company.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	
	public static Double parseDoubleWithBrackets(String doubleStr) {
		if (doubleStr == null) {
			return null;
		}
		doubleStr = doubleStr.trim();
		// Merrill Lynch returns these for N/A.
		if (doubleStr.equals("--") || doubleStr.equals("n/a")) {
			return null;
		}
		
		boolean isNegative = false;
		if (doubleStr.startsWith("(") && doubleStr.endsWith(")")) {
			isNegative = true;
			doubleStr = doubleStr.substring(1, doubleStr.length() - 1);
		}
		double value = Double.parseDouble(doubleStr);
		value = isNegative ? -value : value;
		return value;
	}


	public static Double parseDoubleWithSignAndDollar(String doubleStr) {
		if (doubleStr == null) {
			return null;
		}
		doubleStr = doubleStr.trim();
		// Merrill Lynch returns these for N/A.
		if (doubleStr.equals("--") || doubleStr.equals("n/a")) {
			return null;
		}
		
		boolean isNegative = doubleStr.charAt(0) == '-';
		doubleStr = doubleStr.substring(2);
		double value = Double.parseDouble(doubleStr);
		value = isNegative ? -value : value;
		return value;
	}


	public static Double parseDoubleWithSignAndPercent(String doubleStr) {
		if (doubleStr == null) {
			return null;
		}
		doubleStr = doubleStr.trim();
		// Merrill Lynch returns these for N/A.
		if (doubleStr.equals("--") || doubleStr.equals("n/a")) {
			return null;
		}
		
		boolean isNegative = doubleStr.charAt(0) == '-';
		doubleStr = doubleStr.substring(1, doubleStr.length() - 1);
		double value = Double.parseDouble(doubleStr);
		value = isNegative ? -value : value;
		return value;
	}
	

	public static Long parseIntegerDollarAmount(String str) {
		if (str == null) {
			return null;
		}
		str = str.trim();
		if (str.length() == 0) {
			return null;
		}
		
		str = str.charAt(0) == '$' ? str.substring(1,  str.length()) : str;
		str = str.trim();
		if (str.length() == 0) {
			return null;
		}
		
		str = str.replaceAll(",", "");
		return Long.parseLong(str);
	}

	
	public static String toStringWith2DecimalPlaces(double value) {
		value = value * 100.00D;
		long valueLong = Math.round(value);
		String valueStr = String.valueOf(valueLong);
		int valueStrLength = valueStr.length();
		switch (valueStrLength) {
			case 1: valueStr = "0.0" + valueStr; break;
			case 2: valueStr = "0." + valueStr; break;
			case 3: valueStr = ((valueStr.charAt(0) == '-') ? "-0" : valueStr.substring(0, 1)) + "." + valueStr.substring(1, 3); break;
			default: valueStr = valueStr.substring(0, valueStrLength - 2) + "." + valueStr.substring(valueStrLength - 2);
		}
		return valueStr;
	}
	
	
	private static final Set<String> companyTypeSet = new HashSet<>();
	static {
		companyTypeSet.add("Corp.");
		companyTypeSet.add("Corporation");
		companyTypeSet.add("inc.");
		companyTypeSet.add("Inc");
		companyTypeSet.add("Inc.");
		companyTypeSet.add("Incorporated");
		companyTypeSet.add("Limited");
		companyTypeSet.add("LLC");
		companyTypeSet.add("Ltd.");
		companyTypeSet.add("L.P.");
		companyTypeSet.add("LP");
		companyTypeSet.add("LP.");
		companyTypeSet.add("N.P.");
		companyTypeSet.add("plc");
		companyTypeSet.add("S.A.");
		companyTypeSet.add("(The)");
	}

	public static String stripTrailingCompanyTypeFromName(String name) {
		if (name == null || name.length() == 0) {
			return null;
		}
		name = name.trim();
		
		String[] pieces = name.split(" ");
		int length = pieces.length;
		if (length > 1) {
			length = (companyTypeSet.contains(pieces[length - 1].trim())) ? length - 1 : length;
			name = "";
			for (int i = 0; i < length; i++) {
				name += pieces[i] + " ";
			}
			name = name.trim();
			name = name.endsWith(",") ? name.substring(0, name.length() - 1) : name;
		}
		return name;
	}
	
	
	public static void main(String[] args) {
		String str = toStringWith2DecimalPlaces(1.66);
		System.out.println(str);
	}
}
