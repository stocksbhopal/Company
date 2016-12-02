package com.sanjoyghosh.company.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtils {

	private static final Pattern FidelityHoldingsPattern = Pattern.compile(Constants.FidelityHoldingsFileName);
	private static final SimpleDateFormat FidelityDateFormatter = new SimpleDateFormat("MMM-dd-yyyy");
	
	private static final Pattern MerrillLynchHoldingsPattern = Pattern.compile(Constants.MerrillLynchHoldingsFileName);
	private static final SimpleDateFormat MerrillLynchDateFormatter = new SimpleDateFormat("MMddyyyy");
	
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
	
	private static final SimpleDateFormat alexaDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	
	
	public static Date getDateFromFidelityHoldingsFileName(String fileName) throws ParseException {
		Matcher matcher = FidelityHoldingsPattern.matcher(fileName);
		if (!matcher.matches()) {
			System.err.println("Fidelity Holdings File has bad name: " + fileName);
			return null;
		}
		String dayString = matcher.group(1);
		Date day = FidelityDateFormatter.parse(dayString);
		return day;
	}

	
	public static Date getDateFromMerrillLynchHoldingsFileName(String fileName) throws ParseException {
		Matcher matcher = MerrillLynchHoldingsPattern.matcher(fileName);
		if (!matcher.matches()) {
			System.err.println("Merrill Lynch Holdings File has bad name: " + fileName);
			return null;
		}
		String dayString = matcher.group(1);
		Date day = MerrillLynchDateFormatter.parse(dayString);
		return day;
	}


	/**
	 * Assumes that the date is of the form: "8/27/2015"
	 */
	public static Date getDate(String dateStr) throws ParseException {
		if (dateStr.equals("--") || dateStr.equals("")) {
			return null;
		}
		Date day = dateFormatter.parse(dateStr);
		return day;
	}
	
	
	public static String toDateString(Date date) {
		return dateFormatter.format(date);
	}
	
	
	public static Date getDateFromAlexa(String alexaDateStr) throws ParseException {
		Date date = alexaDateFormatter.parse(alexaDateStr);
		return date;
	}
	
	
	public static void main(String[] args) {
		try {
			Date date = getDateFromFidelityHoldingsFileName("Portfolio_Position_Aug-24-2016.csv");
			System.out.println(dateFormatter.format(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}