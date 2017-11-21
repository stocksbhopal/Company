package com.sanjoyghosh.company.utils;

import java.io.Serializable;
import java.time.LocalDate;

public class LocalDateRange implements Serializable {

	private static final long serialVersionUID = 7516960257871965823L;

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
			return "on " + LocalDateUtils.toDateString(startDate);
		}
		return "between " + LocalDateUtils.toDateString(startDate) + " and " + LocalDateUtils.toDateString(endDate);
	}
}
