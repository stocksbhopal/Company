package com.sanjoyghosh.company.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.CompanyNamePrefix;

public class CompanyJPA {
	
	private static Map<Integer, Company> companyByIdMap;
	private static Map<String, Company> companyBySymbolMap;
	
	
	static {
		companyByIdMap = new HashMap<>();
		companyBySymbolMap = new HashMap<>();
		List<Company> companyList = JPAHelper.getEntityManager().createQuery("SELECT c FROM Company AS c", Company.class).getResultList();
		for (Company company : companyList) {
			companyByIdMap.put(company.getId(), company);
			companyBySymbolMap.put(company.getSymbol().toUpperCase(), company);
		}
	}
	
	
	private static final Set<String> companyTypeSet = new HashSet<>();
	static {
		companyTypeSet.add("Co".toLowerCase());
		companyTypeSet.add("Co.".toLowerCase());
		companyTypeSet.add("Co.,".toLowerCase());
		companyTypeSet.add("Company,".toLowerCase());
		companyTypeSet.add("Corp".toLowerCase());
		companyTypeSet.add("Corp.".toLowerCase());
		companyTypeSet.add("Corporation".toLowerCase());
		companyTypeSet.add("Inc".toLowerCase());
		companyTypeSet.add("Inc.".toLowerCase());
		companyTypeSet.add("Incorporated".toLowerCase());
		companyTypeSet.add("Limited".toLowerCase());
		companyTypeSet.add("LLC".toLowerCase());
		companyTypeSet.add("LTD".toLowerCase());
		companyTypeSet.add("LTD.".toLowerCase());
		companyTypeSet.add("L.P.".toLowerCase());
		companyTypeSet.add("LP".toLowerCase());
		companyTypeSet.add("LP.".toLowerCase());
		companyTypeSet.add("N.P.".toLowerCase());
		companyTypeSet.add("NV".toLowerCase());
		companyTypeSet.add("PLC".toLowerCase());
		companyTypeSet.add("PLC.".toLowerCase());
		companyTypeSet.add("S.A.".toLowerCase());
		companyTypeSet.add("SA".toLowerCase());
		companyTypeSet.add("(The)".toLowerCase());
	}

	private static String stripTrailingCompanyTypeFromName(String name) {
		if (name == null || name.trim().length() == 0) {
			return null;
		}
		name = name.trim().toLowerCase();
		if (name.startsWith("the")) {
			name = name.substring("the".length()).trim();
		}
		
		String[] pieces = name.split(" ");
		int length = pieces.length;
		if (length > 1) {
			length = (companyTypeSet.contains(pieces[length - 1].trim())) ? length - 1 : length;
			// Check again for companies like Home Depot, Inc. (The)
			if (length > 1) {
				length = (companyTypeSet.contains(pieces[length - 1].trim())) ? length - 1 : length;
			}
			name = "";
			// This skips over tiny first words like El, A, An, etc.
			for (int i = (pieces[0].length() <= 2 ? 1 : 0); i < length; i++) {
				// Mostly to strip .com from Amazon.com.
				pieces[i] = (pieces[i].endsWith(".com")) ? (pieces[i].substring(0, pieces[i].length() - ".com".length())) : pieces[i];
				pieces[i] = (pieces[i].endsWith(",")) ? (pieces[i].substring(0, pieces[i].length() - ",".length())) : pieces[i];
				// For Dun & Bradstreet
				pieces[i] = (pieces[i].equals("&")) ? "and" : pieces[i];
				// To take the apostrophe out of dick&#39;s.
				if (pieces[i].indexOf("&#39;") > 0) {
					String[] parts = pieces[i].split("&#39;");
					pieces[i] = (parts.length == 2) ? (parts[0] + parts[1]) : parts[0];
				}
				// archer-daniels-midland company
				pieces[i] = pieces[i].replaceAll("-", " ");
				name += pieces[i] + " ";
			}
			name = name.trim();
		}
		return name;
	}

	
	public static void loadCompanyNamePrefix() {
		EntityManager em = JPAHelper.getEntityManager();
		em.getTransaction().begin();
		
		List<Company> companyList = em.createQuery("SELECT c FROM Company AS c ORDER BY c.symbol ASC", Company.class).getResultList();
		for (Company company : companyList) {
			String name = stripTrailingCompanyTypeFromName(company.getName());
			String[] pieces = name.split(" ");
			String namePrefix = "";
			for (String piece : pieces) {
				namePrefix += " " + piece;
				namePrefix = namePrefix.trim();

				CompanyNamePrefix cnp = new CompanyNamePrefix();
				cnp.setCompany(company);
				cnp.setCompanyNamePrefix(namePrefix);
				cnp.setManuallyAdded(false);
				em.persist(cnp);
				System.out.println("Persisted: " + namePrefix);
			}
		}
		
		System.out.println("About to Commit");
		em.getTransaction().commit();
	}
		

	public static List<Company> fetchCompanyListByNamePrefix(String namePrefix) {
		try {
			List<CompanyNamePrefix> cnfList = 
				JPAHelper.getEntityManager().createQuery("SELECT c FROM CompanyNamePrefix AS c WHERE c.companyNamePrefix = :namePrefix", CompanyNamePrefix.class)
				.setParameter("namePrefix", namePrefix)
				.getResultList();
			List<Company> companyList = new ArrayList<>();
			for (CompanyNamePrefix cnf : cnfList) {
				companyList.add(cnf.getCompany());
			}
			return companyList;
		}
		catch (NoResultException e) {
			return null;
		}
	}

	
	public static Company fetchCompanyBySymbol(String symbol) {
		try {
			Company company = JPAHelper.getEntityManager().createQuery("SELECT c FROM Company AS c WHERE c.symbol = :symbol", Company.class)
				.setParameter("symbol", symbol)
				.getSingleResult();
			return company;
		}
		catch (NoResultException e) {
			return null;
		}
	}
}
