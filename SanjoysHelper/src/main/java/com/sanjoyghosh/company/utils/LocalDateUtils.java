package com.sanjoyghosh.company.utils;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateUtils {
	
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");


	/**
	 * Assumes that the date is of the form: "8/27/2015"
	 */
	public static LocalDate getLocalDate(String dateStr) throws ParseException {
		if (dateStr.equals("--") || dateStr.equals("")) {
			return null;
		}
		LocalDate day = LocalDate.parse(dateStr, dateFormatter);
		return day;
	}
	
	
	public static String toDateString(LocalDate date) {
		return date.format(dateFormatter);
	}
}
