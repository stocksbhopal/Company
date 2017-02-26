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


	@Override
	public String toString() {
		return "LocalDateRange [startDate=" + startDate + ", endDate=" + endDate + ", numberOfDays=" + numberOfDays
				+ "]";
	}
}
