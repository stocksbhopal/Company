package com.sanjoyghosh.company.db;

import java.util.List;

import javax.persistence.EntityManager;

import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.utils.StringUtils;

public class CompanySpeechNameUpdater {

	public static void main(String[] args) {
		EntityManager em = JPAHelper.getEntityManager();
		List<Company> companyList = CompanyUtils.fetchAllCompany(em);
		for (Company company : companyList) {
			em.getTransaction().begin();
			company.setSpeechName(StringUtils.stripTrailingCompanyTypeFromName(company.getName()));
			em.persist(company);
			em.getTransaction().commit();
			System.out.println(company.getSpeechName());
		}
		System.out.println("Done");
		System.exit(0);
	}
}
