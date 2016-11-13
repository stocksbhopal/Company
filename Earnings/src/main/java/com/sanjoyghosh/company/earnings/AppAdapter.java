package com.sanjoyghosh.company.earnings;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.sanjoyghosh.company.api.CompanyApi;
import com.sanjoyghosh.company.api.CompanyEarnings;

public class AppAdapter {

	public static Object[][] getAllCompanyEarnings() {
		List<CompanyEarnings> companyEarningsList = CompanyApi.getCompanyEarnings(new GregorianCalendar(2016, Calendar.NOVEMBER, 12), 7);
		Object[][] data = new Object [companyEarningsList.size()][];
		for (int i = 0; i < companyEarningsList.size(); i++) {
			CompanyEarnings ce = companyEarningsList.get(i);
			Object[] earnings = new Object[3];
			earnings[0] = ce.getSymbol();
			earnings[1] = ce.getName();
			earnings[2] = ce.getEarningsDate();
			data[i] = earnings;
		}
		return data;
	}
}
