package com.sanjoyghosh.company.dynamodb.nasdaq;

import java.util.HashSet;
import java.util.Set;

public class CompanyNameMatcher {

	private static final Set<String> CompanyStopWords = new HashSet<>();
	static {
		CompanyStopWords.add("A".toLowerCase());
		CompanyStopWords.add("An".toLowerCase());
		CompanyStopWords.add("Co".toLowerCase());
		CompanyStopWords.add("Co.".toLowerCase());
		CompanyStopWords.add("Co.,".toLowerCase());
		CompanyStopWords.add("Company".toLowerCase());
		CompanyStopWords.add("Company,".toLowerCase());
		CompanyStopWords.add("Corp".toLowerCase());
		CompanyStopWords.add("Corp.".toLowerCase());
		CompanyStopWords.add("Corporation".toLowerCase());
		CompanyStopWords.add("El".toLowerCase());
		CompanyStopWords.add("Inc".toLowerCase());
		CompanyStopWords.add("Inc.".toLowerCase());
		CompanyStopWords.add("Incorporated".toLowerCase());
		CompanyStopWords.add("Limited".toLowerCase());
		CompanyStopWords.add("LLC".toLowerCase());
		CompanyStopWords.add("LTD".toLowerCase());
		CompanyStopWords.add("LTD.".toLowerCase());
		CompanyStopWords.add("L.P.".toLowerCase());
		CompanyStopWords.add("LP".toLowerCase());
		CompanyStopWords.add("LP.".toLowerCase());
		CompanyStopWords.add("N.P.".toLowerCase());
		CompanyStopWords.add("NV".toLowerCase());
		CompanyStopWords.add("PLC".toLowerCase());
		CompanyStopWords.add("PLC.".toLowerCase());
		CompanyStopWords.add("S.A.".toLowerCase());
		CompanyStopWords.add("SA".toLowerCase());
		CompanyStopWords.add("(The)".toLowerCase());
	}

	
	public static String stripStopWordsFromName(String name) {
		if (name == null || name.trim().length() == 0) {
			return null;
		}
		name = name.trim().toLowerCase();
		
		// THE FOLLOWING REPLACEMENTS HAVE TO BE DONE
		// IN THE SAME ORDER.
		// archer-daniels-midland company
		name = name.replaceAll("-", " ");
		// apostrophe
		name = name.replaceAll("&#39;", "");
		// amazon.com
		name = name.replaceAll("\\.com ", " ");
		// home depot, inc. (the)
		name = name.replaceAll(",", "");
		// remove the .s for name abbreviations
		name = name.replaceAll("\\.", "");
		// alexa doesn't know &. expand with blanks
		name = name.replaceAll(" & ", " and ");
		// alexa doesn't know &. expand with blanks
		name = name.replaceAll("&", "and");
		
		String[] pieces = name.split(" ");
		int length = pieces.length;
		String strippedName = "";
		for (int i = 0; i < length; i++) {
			String word = pieces[i];
			if (!CompanyStopWords.contains(word)) {
				strippedName += word + " ";
			}
		}

		name = strippedName.length() > 0 ? strippedName.trim() : name;
		// Make sure the name doesn't end with "and" after stripping the stop words.
		name = name.endsWith("and") ? name.substring(0,  name.length() - "and".length()).trim() : name;
		
		return name;
	}
}
