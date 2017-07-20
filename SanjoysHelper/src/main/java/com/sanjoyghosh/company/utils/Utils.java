package com.sanjoyghosh.company.utils;

public class Utils {

	public static int toInt(Double valueDouble) {
		if (valueDouble == null) {
			return 0;
		}
		return (int)(double)valueDouble;
	}
}
