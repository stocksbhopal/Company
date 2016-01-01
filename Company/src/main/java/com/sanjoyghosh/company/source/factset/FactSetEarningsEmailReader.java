package com.sanjoyghosh.company.source.factset;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;

import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.StringUtils;
import com.sanjoyghosh.company.db.model.EarningsDate;

public class FactSetEarningsEmailReader {

	private EntityManager entityManager;
	
	private SimpleDateFormat fullDateParser = new SimpleDateFormat("MMMM dd, yyyy");
	private SimpleDateFormat dateMonthParser = new SimpleDateFormat("dd-MMM");
	private List<String> datePrefixList = Arrays.asList("Date:");
	private List<String> daysOfTheWeekList = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
	private List<String> symbolsPrefixList = Arrays.asList("Earnings:", "Pre-open", "Post-close");
	
	
	public FactSetEarningsEmailReader() {}
	
	
	private void readFactSetEarningsEmail(File factSetEmail) throws IOException, ParseException {
		LineNumberReader reader = null;
		try {
			String line = null;
			Calendar emailDate = null;
			Calendar earningsDate = null;
			
			reader = new LineNumberReader(new FileReader(factSetEmail));
			while ((line = reader.readLine()) != null) {
				String fullDateStr = StringUtils.remainderWithStartPrefix(line, datePrefixList);
				if (fullDateStr != null) {
					emailDate = new GregorianCalendar();
					emailDate.setTime(fullDateParser.parse(fullDateStr));
					continue;
				}

				String dateMonthStr = StringUtils.remainderWithStartPrefix(line, daysOfTheWeekList);
				if (dateMonthStr != null) {
					earningsDate = new GregorianCalendar();
					earningsDate.setTime(dateMonthParser.parse(dateMonthStr));
					earningsDate.set(Calendar.YEAR, emailDate.get(Calendar.YEAR));	
					continue;
				}
				
				String symbolListStr = StringUtils.remainderWithInsidePrefix(line, symbolsPrefixList);
				if (symbolListStr != null) {
					StringTokenizer tokenizer = new StringTokenizer(symbolListStr, ",");
					while (tokenizer.hasMoreTokens()) {
						String symbol = tokenizer.nextToken().trim();
						EarningsDate earningsDateDB = CompanyUtils.fetchEarningsDateForSymbolDate(entityManager, symbol, new Timestamp(earningsDate.getTime().getTime()));
						if (earningsDateDB != null) {
							earningsDateDB.setIsFactSet(true);
							entityManager.persist(earningsDateDB);
						}
					}
					continue;
				}
			}
		}
		finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	
	private void readFactSetEarningsEmailFiles() {
		entityManager = JPAHelper.getEntityManager();
		
		File downloadsFolder = new File("/Users/sanjoyghosh/Downloads");
		File[] factSetEmails = downloadsFolder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith("FactSet StreetAccount Summary - Week Ahead") && name.endsWith(".txt");
			}
		});
		
		for (File factSetEmail : factSetEmails) {
			entityManager.getTransaction().begin();
			try {
				readFactSetEarningsEmail(factSetEmail);
				entityManager.getTransaction().commit();
			} 
			catch (FileNotFoundException e) {
				entityManager.getTransaction().rollback();
				e.printStackTrace();
			} 
			catch (IOException e) {
				entityManager.getTransaction().rollback();
				e.printStackTrace();
			} 
			catch (ParseException e) {
				// TODO Auto-generated catch block
				entityManager.getTransaction().rollback();
				e.printStackTrace();
			}
		}
	}
	
	
	public static void main(String[] args) {
		FactSetEarningsEmailReader reader = new FactSetEarningsEmailReader();
		reader.readFactSetEarningsEmailFiles();
		System.exit(0);
	}
}
