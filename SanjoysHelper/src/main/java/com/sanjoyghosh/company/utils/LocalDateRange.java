package com.sanjoyghosh.company.utils;

import java.time.LocalDate;

public class LocalDateRange {

	private LocalDate startDate;
	private LocalDate endDate;
	private int numberOfDays;
	
	
	public LocalDateRange(LocalDate startDate, LocalDate endDate, int numberOfDays) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.numberOfDays = numberOfDays;
	}


	public LocalDate getStartDate() {
		return startDate;
	}


	public LocalDate getEndDate() {
		return endDate;
	}


	public int getNumberOfDays() {
		return numberOfDays;
	}


	public String toAlexaString() {
		if (numberOfDays == 1) {
			return "on " + DateUtils.toDateString(startDate);
		}
		return "between " + DateUtils.toDateString(startDate) + " and " + DateUtils.toDateString(endDate);
	}
}
