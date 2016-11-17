package com.sanjoyghosh.company.api;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
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
	
	
	private static CompanyEarnings toCompanyEarnings(int serialNum, EarningsDate earningsDate) {
		Company company = companyByIdMap.get(earningsDate.getCompanyId());
		CompanyEarnings ce = null;
		if (company != null) {
			ce = new CompanyEarnings(serialNum, company.getSymbol(), company.getName(), earningsDate.getEarningsDate(), 
				earningsDate.getBeforeMarketOrAfterMarket(), earningsDate.getCompanyId(), earningsDate.getId());
		}
		else {
			ce = new CompanyEarnings(serialNum, "NOSY", "No Company", earningsDate.getEarningsDate(), 
				earningsDate.getBeforeMarketOrAfterMarket(), earningsDate.getCompanyId() == null ? -1 : earningsDate.getCompanyId(), earningsDate.getId());
		}
		return ce;
	}
	
	
	public static List<ITableItem> getCompanyEarnings(GregorianCalendar startDate, int numberOfDaysForward) {
		Timestamp start = new Timestamp(startDate.getTimeInMillis());
		
		GregorianCalendar endDate = new GregorianCalendar();
		endDate.setTime(start);
		endDate.add(Calendar.DAY_OF_MONTH, numberOfDaysForward);
		Timestamp end = new Timestamp(endDate.getTimeInMillis());
		
		List<CompanyEarnings> earningsDateList = CompanyUtils.fetchAllEarningsDateForDateRange(entityManager, start, end);
		/*
		earningsDateList.sort(new Comparator<EarningsDate>() {
			@Override
			public int compare(EarningsDate o1, EarningsDate o2) {
				if (o1.getEarningsDate() != o2.getEarningsDate()) {
					return o1.getEarningsDate().compareTo(o2.getEarningsDate());
				}
				if (o1.getBeforeMarketOrAfterMarket() != o2.getBeforeMarketOrAfterMarket()) {
					return o1.getBeforeMarketOrAfterMarket().compareTo(o2.getBeforeMarketOrAfterMarket());
				}
				CompanyEarnings ce1 = toCompanyEarnings(-1, o1);
				CompanyEarnings ce2 = toCompanyEarnings(-1, o2);
				return ce1.getName().compareToIgnoreCase(ce2.getName());
			}
		});
		*/
		
		int serialNum = 1;
		List<ITableItem> companyEarningsList = new ArrayList<ITableItem>(earningsDateList.size());
		for (CompanyEarnings ed : earningsDateList) {
//			CompanyEarnings ce = toCompanyEarnings(serialNum++, ed);
			companyEarningsList.add(ed);
		}
				
		return companyEarningsList;
	}
}
