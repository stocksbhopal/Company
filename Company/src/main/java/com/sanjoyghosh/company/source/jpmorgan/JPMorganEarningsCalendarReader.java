package com.sanjoyghosh.company.source.jpmorgan;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.utils.StringUtils;

public class JPMorganEarningsCalendarReader {
	
	private SimpleDateFormat dateParser = new SimpleDateFormat("dd-MMM-yy");
	private List<String> opinionList = Arrays.asList("US Overweight", "US Neutral", "US Underweight", "US Not Rated", "US OW", "US N", "US UW", "US NR");
	
	
	public JPMorganEarningsCalendarReader() {}
	
	
	private void readJPMEarningsCalendar(String earningsCalendar, EntityManager entityManager) throws IOException {
		String line = null;
		LineNumberReader reader = new LineNumberReader(new StringReader(earningsCalendar));
		while ((line = reader.readLine()) != null) {
			Date earningsDate = null;
			try {
				earningsDate = dateParser.parse(line);
			} 
			catch (ParseException e) {
				continue;
			}
			
			if (earningsDate != null && line.length() > 24) {
				String[] prefixAndSuffix = StringUtils.prefixAndSuffixWithEmbedded(line, opinionList);
				if (prefixAndSuffix != null) {
					String prefix = prefixAndSuffix[0];
					String embedded = prefixAndSuffix[1];
					String suffix = prefixAndSuffix[2];
					
					int pos = prefix.lastIndexOf(' ');
					String symbol = prefix.substring(pos, prefix.length()).trim();
					if (symbol.equals("BTUUQ")) {
						continue;
					}
					
					Company company = CompanyUtils.fetchCompanyBySymbol(entityManager, symbol);
					if (company != null) {
						pos = embedded.indexOf(' ');
						String opinion = embedded.substring(pos, embedded.length()).trim();
						pos = suffix.indexOf('$');
						String analyst = suffix.substring(0, pos).trim();
						
						company.setJpmOpinion(opinion);
						company.setJpmAnalyst(analyst);
						System.out.println("Company: " + symbol);
						entityManager.persist(company);
					}
				}
			}
		}
	}
	
	
	private void readJPMEarningsCalendarFile(File jpmEarningsFile, EntityManager entityManager) throws IOException {
		FileInputStream fis = null;
	    Tika tika = new Tika();
	    try {
	    	fis = new FileInputStream(jpmEarningsFile);
			String contents = tika.parseToString(fis);
			readJPMEarningsCalendar(contents, entityManager);
		} 
	    catch (TikaException e) {
			e.printStackTrace();
		}
	    finally {
	    	if (fis != null) {
	    		fis.close();
	    	}
	    }
	}
	
	
	private void readAllJPMorganEarningsCalendarFiles(EntityManager entityManager) throws Exception {		
		File jpmEarningsFolder = new File("/Users/sanjoyghosh/Downloads/JPMEarnings");
		for (File jpmEarningsFile : jpmEarningsFolder.listFiles()) {
			try {
				readJPMEarningsCalendarFile(jpmEarningsFile, entityManager);
			} 
			catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
	}
	
	
	public static void main(String[] args) {
		List<String> mySQLHostList = JPAHelper.getMySQLHostList();
		for (String mySQLHost : mySQLHostList) {
			EntityManager entityManager = null;
			try {
				entityManager = JPAHelper.getEntityManager(mySQLHost);
				entityManager.getTransaction().begin();
				
				JPMorganEarningsCalendarReader reader = new JPMorganEarningsCalendarReader();
				reader.readAllJPMorganEarningsCalendarFiles(entityManager);
				
				entityManager.getTransaction().commit();
			}
			catch (Exception e) {
				e.printStackTrace();
				if (entityManager.getTransaction().isActive()) {
					entityManager.getTransaction().rollback();
				}
			}
			finally {
				if (entityManager != null) {
					entityManager.close();
				}
			}
		}
		
		System.exit(0);
	}
}
