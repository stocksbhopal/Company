package com.sanjoyghosh.company.utils;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {

    private static final Logger log = LoggerFactory.getLogger(DateUtils.class);
	
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private static final DateTimeFormatter alexaDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	
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
	
	
	public static LocalDate getDateFromAlexa(String alexaDateStr) throws ParseException {
		LocalDate date = LocalDate.parse(alexaDateStr, alexaDateFormatter);
		log.info("DateUtils: date: " + date.toString());
		return date;
	}
}
