package com.sanjoyghosh.company.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {

    private static final Logger log = LoggerFactory.getLogger(DateUtils.class);

    
	private static final Pattern FidelityHoldingsPattern = Pattern.compile(Constants.FidelityHoldingsFileName);
	private static final SimpleDateFormat FidelityDateFormatter = new SimpleDateFormat("MMM-dd-yyyy");
	
	private static final Pattern MerrillLynchHoldingsPattern = Pattern.compile(Constants.MerrillLynchHoldingsFileName);
	private static final SimpleDateFormat MerrillLynchDateFormatter = new SimpleDateFormat("MMddyyyy");
	
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private static final DateTimeFormatter alexaDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	
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
