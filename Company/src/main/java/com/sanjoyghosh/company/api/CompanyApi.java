package com.sanjoyghosh.company.api;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.EarningsDate;

public class CompanyApi {

	private static EntityManager			entityManager;
	private static Map<Integer, Company> 	companyByIdMap;
	
	
	public static void init() {
		entityManager = JPAHelper.getEntityManager();
		companyByIdMap = CompanyUtils.fetchAllCompanyByIdMap(entityManager);
	}
	
	
	private static CompanyEarnings toCompanyEarnings(EarningsDate earningsDate) {
		Company company = companyByIdMap.get(earningsDate.getCompanyId());
		CompanyEarnings ce = null;
		if (company != null) {
			ce = new CompanyEarnings(company.getSymbol(), company.getName(), earningsDate.getEarningsDate(), 
				earningsDate.getBeforeMarketOrAfterMarket(), earningsDate.getCompanyId(), earningsDate.getId());
		}
		else {
			ce = new CompanyEarnings("NOSY", "No Company", earningsDate.getEarningsDate(), 
				earningsDate.getBeforeMarketOrAfterMarket(), earningsDate.getCompanyId() == null ? -1 : earningsDate.getCompanyId(), earningsDate.getId());
		}
		return ce;
	}
	
	
	public static List<ITableItem> getCompanyEarnings(GregorianCalendar startDate, int numberOfDaysForward) {
		Timestamp start = new Timestamp(startDate.getTimeInMillis());
		startDate.add(Calendar.DAY_OF_MONTH, numberOfDaysForward);
		Timestamp end = new Timestamp(startDate.getTimeInMillis());
		
		List<EarningsDate> earningsDateList = CompanyUtils.fetchAllEarningsDateForDateRange(entityManager, start, end);
		List<ITableItem> companyEarningsList = new ArrayList<ITableItem>(earningsDateList.size());
		for (EarningsDate ed : earningsDateList) {
			CompanyEarnings ce = toCompanyEarnings(ed);
			companyEarningsList.add(ce);
		}
		return companyEarningsList;
	}
}
