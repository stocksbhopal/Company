package com.sanjoyghosh.company.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sanjoyghosh.company.source.merrilllynch.MerrillLynchHoldingsReader;

public class DateUtils {

	private static final Pattern MerrillLynchHoldingsPattern = Pattern.compile(MerrillLynchHoldingsReader.MerrillLynchHoldingsFileName);
	private static final SimpleDateFormat dayFormatter = new SimpleDateFormat("MMddyyyy");
	
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
}
