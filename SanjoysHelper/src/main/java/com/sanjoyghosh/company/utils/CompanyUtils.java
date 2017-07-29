package com.sanjoyghosh.company.utils;

import javax.persistence.EntityManager;

import com.sanjoyghosh.company.db.CompanyJPA;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.earnings.intent.AllSlotValues;

public class CompanyUtils {

	public static Company getCompany(AllSlotValues slotValues) {
		EntityManager em = null;
		try {
			em = JPAHelper.getEntityManager();
	    	Company company = CompanyJPA.fetchCompanyByNameOrSymbol(em, slotValues);
	    	return company;
		}
		finally {
			if (em != null) {
				em.close();
			}
		}
	}
}
