package com.sanjoyghosh.company.utils;

import com.amazon.speech.speechlet.Session;

public class LoggerUtils {

	public static String makeLogString(Session session, String string) {
		return session.getUser().getUserId() + " $$ " + string;
	}
}
