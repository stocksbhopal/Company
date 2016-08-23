package com.sanjoyghosh.company.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtils {

	private static final Pattern MerrillLynchHoldingsPattern = Pattern.compile(Constants.MerrillLynchHoldingsFileName);
	private static final SimpleDateFormat dayFormatter = new SimpleDateFormat("MMddyyyy");
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
	
	
	/**
	 * Assumes that the file name is of the form: "Holdings_([0-9]{8}).csv"
	 */
	public static Date getDateFromMLHoldingsFileName(String mlFileName) throws ParseException {
		Matcher matcher = MerrillLynchHoldingsPattern.matcher(mlFileName);
		if (!matcher.matches()) {
			System.err.println("Merrill Lynch Holdings File has bad name: " + mlFileName);
			return null;
		}
		String dayString = matcher.group(1);
		Date day = dayFormatter.parse(dayString);
		return day;
	}


	/**
	 * Assumes that the date is of the form: "8/27/2015"
	 */
	public static Date getDate(String dateStr) throws ParseException {
		if (dateStr.equals("--")) {
			return null;
		}
		Date day = dateFormatter.parse(dateStr);
		return day;
	}
	
	
	public static void main(String[] args) {
		try {
			Date date = getDate("8/18/2015");
			System.out.println(dateFormatter.format(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
