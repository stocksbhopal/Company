package com.sanjoyghosh.company.utils;

import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateUtils {
	
	private static final DateTimeFormatter DateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private static final DateTimeFormatter ReutersDateTimeFormatter = DateTimeFormatter.ofPattern("MMddyyyy");
	private static final DateTimeFormatter GoogleFinanceDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-M-dd");

	
	public static LocalDate getWeekdayBefore(LocalDate date, int daysBefore) {
		if (date == null) {
			return null;
		}
		if (daysBefore <= 0) {
			return date;
		}
		for (int i = 0; i < daysBefore; i++) {
			date = date.minusDays(1);
			if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
				date = date.minusDays(2);
			}
			else if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
				date = date.minusDays(1);
			}
		}
		return date;
	}
	

	public static LocalDate getWeekdayAfter(LocalDate date, int daysAfter) {
		if (date == null) {
			return null;
		}
		if (daysAfter <= 0) {
			return date;
		}
		for (int i = 0; i < daysAfter; i++) {
			date = date.plusDays(1);
			if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
				date = date.plusDays(2);
			}
			else if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
				date = date.plusDays(1);
			}
		}
		return date;
	}

	
	/**
	 * Assumes that the date is of the form: "8/27/2015"
	 */
	public static LocalDate getLocalDate(String dateStr) throws ParseException {
		if (dateStr.equals("--") || dateStr.equals("")) {
			return null;
		}
		LocalDate day = LocalDate.parse(dateStr, DateFormatter);
		return day;
	}
	
	
	public static String toDateString(LocalDate date) {
		return date.format(DateFormatter);
	}
	
	public static String toReutersDateString(LocalDate date) {
		return date.format(ReutersDateTimeFormatter);
	}
	
	public static String toReutersDateString() {
		return toReutersDateString(LocalDate.now());
	}
	
	public static String toGoogleFinanceDateString() {
		return toGoogleFinanceDateString(LocalDate.now());
	}

	public static String toGoogleFinanceDateString(LocalDate date) {
		return date.format(GoogleFinanceDateTimeFormatter);
	}
}
