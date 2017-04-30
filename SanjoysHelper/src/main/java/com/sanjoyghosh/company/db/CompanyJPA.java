package com.sanjoyghosh.company.db;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.db.model.CompanyNamePrefix;
import com.sanjoyghosh.company.earnings.intent.AllSlotValues;

public class CompanyJPA {
	
    private static final Logger logger = Logger.getLogger(CompanyJPA.class.getName());

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

	public static String stripTrailingCompanyTypeFromName(String name) {
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

	
	public static Company fetchCompanyByNameOrSymbol(EntityManager em, AllSlotValues slotValues) {
		String cs = slotValues.getCompanyOrSymbol();
		String css = slotValues.getCompanyOrSymbolSpelt();

		if (cs != null) {
			List<CompanyNamePrefix> cnpList = fetchCompanyListByNamePrefix(em, cs);
			if (cnpList != null && cnpList.size() > 0) {
				if (cnpList.size() > 1) {
					logger.log(Level.WARNING, "Found more than 1 company for: " + cs);
				}
				return cnpList.get(0).getCompany();
			}
		}
		
		if (cs != null) {
			Company company = fetchCompanyBySymbol(em, cs);
			if (company != null) {
				return company;
			}
		}
		
		if (css != null) {
			Company company = fetchCompanyBySymbol(em, css);
			if (company != null) {
				return company;
			}
		}
		
		if (css != null) {
			List<CompanyNamePrefix> cnpList = fetchCompanyListByNamePrefix(em, css);
			if (cnpList != null && cnpList.size() > 0) {
				if (cnpList.size() > 1) {
					logger.log(Level.WARNING, "Found more than 1 company for: " + cs);
				}
				return cnpList.get(0).getCompany();
			}
		}
		
		return null;
	}
	
	
	public static List<CompanyNamePrefix> fetchCompanyListByNamePrefix(EntityManager em, String namePrefix) {
		try {
			List<CompanyNamePrefix> cnfList = 
				em.createQuery("SELECT c FROM CompanyNamePrefix AS c WHERE c.companyNamePrefix = :namePrefix", CompanyNamePrefix.class)
				.setParameter("namePrefix", namePrefix.toLowerCase())
				.getResultList();
			return cnfList;
		}
		catch (NoResultException e) {
			return null;
		}
	}

	
	public static Company fetchCompanyBySymbol(EntityManager em, String symbol) {
		try {
			Company company = em.createQuery("SELECT c FROM Company AS c WHERE c.symbol = :symbol", Company.class)
				.setParameter("symbol", symbol.toUpperCase())
				.getSingleResult();
			return company;
		}
		catch (NoResultException e) {
			return null;
		}
	}
}
