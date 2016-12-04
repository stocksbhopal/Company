package com.sanjoyghosh.company.earnings.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
	
	private static final SimpleDateFormat alexaDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	

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
}
