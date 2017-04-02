package com.sanjoyghosh.company.utils;

public class LoggerUtils {

	public static String makeLogString(String alexaUserId, String string) {
		return alexaUserId + " $$ " + string;
	}
}
