package com.sanjoyghosh.company.utils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.SignStyle;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAccessor;

public class AlexaDateUtils {

	private static DateTimeFormatter yearMonthDayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
	private static DateTimeFormatter weekFormatter = DateTimeFormatter.ofPattern("yyyy-Www");
	// Taken from the DateTimeFormatterBuilder class code for ISO_WEEK_DATE formatter.
    static {
    	weekFormatter = new DateTimeFormatterBuilder()
    		.parseCaseInsensitive()
            .appendValue(IsoFields.WEEK_BASED_YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral("-W")
            .appendValue(IsoFields.WEEK_OF_WEEK_BASED_YEAR, 2)
            .toFormatter();
    }

    
    /**
     * We only parse single days like today, tomorrow, yesterday, etc.
     * Or days specified by day, month, and maybe year.
     * Or months specified again by this month, last month, etc.
     * Or months specified as November, December, etc.
     * Or weeks specified as this week, last week, next week, etc.
     * Anything else is not handled by Finance Helper.
     */
    public static LocalDateRange getLocalDateRange(String dateStr) {
    	// millennium turns into an empty Date slot value.
    	if (dateStr == null || dateStr.trim().length() == 0) {
    		return null;
    	}
    	dateStr = dateStr.trim();
    	
    	try {
    		LocalDate localDate = LocalDate.parse(dateStr, yearMonthDayFormatter);
    		LocalDateRange localDateRange = new LocalDateRange(localDate, localDate, 1);
    		return localDateRange;
    	}
    	catch (DateTimeParseException e) {}
    	
    	try {
    		YearMonth yearMonth = YearMonth.parse(dateStr, yearMonthFormatter);
    		LocalDateRange localDateRange = new LocalDateRange(yearMonth.atDay(1), yearMonth.atEndOfMonth(), yearMonth.lengthOfMonth());
    		return localDateRange;
    	}
    	catch (DateTimeParseException e) {}

    	try {
	    	TemporalAccessor ta = weekFormatter.parse(dateStr);
	    	int year = ta.get(IsoFields.WEEK_BASED_YEAR);
	    	int week = (int)ta.getLong(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
	    	LocalDate startDate = LocalDate.ofYearDay(year, (week - 1) * 7 + 1);
	    	LocalDate endDate = LocalDate.ofYearDay(year, (week - 1) * 7 + 7);
	    	LocalDateRange localDateRange = new LocalDateRange(startDate, endDate, 7);
	    	return localDateRange;
    	}
    	catch (DateTimeParseException e) {}
    	
    	// Return null for all unhandled Alexa Date specifications.
    	return null;
    }
}
