package com.sanjoyghosh.company.source.merrilllynch;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Activity;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.utils.Constants;
import com.sanjoyghosh.company.utils.DateUtils;
import com.sanjoyghosh.company.utils.StringUtils;

public class MerrillLynchActivityReader {
		
	private EntityManager entityManager;
	private Set<String> transactionTypeSet;
	private Map<Date, Set<Activity>> activityByDateMap;
	
	
	public MerrillLynchActivityReader() {
		entityManager = JPAHelper.getEntityManager();
		transactionTypeSet = new HashSet<String>();
		activityByDateMap = new HashMap<Date, Set<Activity>>();
	}
	
	
	private File[] getMerrillLynchActivityFiles() {
		File[] merrillLynchFiles = Constants.DownloadsFolder.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return (pathname.getName().matches(Constants.MerrillLynchSettledActivityFileName) ||
						pathname.getName().matches(Constants.MerrillLynchPendingAndSettledActivityFileName));
			}
		});
		return merrillLynchFiles;
	}
	
	
	private void readMerrillLynchActivityFile(File merrillLynchFile) {
		entityManager.getTransaction().begin();

		Reader reader = null;
		try {
			reader = new FileReader(merrillLynchFile);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
			for (CSVRecord record : records) {
				if (record.size() == 13) {
					Date tradeDate = DateUtils.getLocalDate(record.get("Trade Date").trim());
					Date settledDate = DateUtils.getLocalDate(record.get("Settlement Date").trim());
					tradeDate = tradeDate == null ? settledDate : tradeDate;
					String account = StringUtils.onlyLast4Characters(record.get("Account #").trim());
					String transactionType = record.get("Description 1 ").trim();
				    String symbol = record.get("Symbol/CUSIP #").trim();
				    String quantityStr = record.get("Quantity");
				    Double quantity = quantityStr.equals("--") ? null : Double.parseDouble(quantityStr.replaceAll(",", "").trim());
				    String priceStr = record.get("Price ($)");
				    Double price = priceStr.equals("--") ? null : Double.parseDouble(priceStr.replaceAll(",", "").trim());
				    Company company = CompanyUtils.fetchCompanyBySymbol(entityManager, symbol);
				    Double amount = StringUtils.parseDoubleWithBrackets(record.get("Amount ($)").replaceAll(",", "").trim());
	
				    Activity activity = new Activity();
				    activity.setCompanyId(company == null ? 0 : company.getId());
				    activity.setTradeDate(new Timestamp(tradeDate.getTime()));
				    activity.setSettledDate(new Timestamp(settledDate.getTime()));
				    activity.setAccount(account);
				    activity.setBrokerage(Constants.MerrillLynchBrokerage);
				    activity.setSymbol(symbol);
				    activity.setTransactionType(transactionType);
				    activity.setQuantity(quantity);
				    activity.setPrice(price);
				    activity.setAmount(amount);

				    if (company != null) {
				    	Set<Activity> activitySet = activityByDateMap.get(settledDate);
				    	if (activitySet == null) {
				    		activitySet = CompanyUtils.fetchAllActivityAtBrokerageForSettledDate(entityManager, Constants.MerrillLynchBrokerage, new Timestamp(settledDate.getTime()));
				    		activityByDateMap.put(settledDate, activitySet);
				    	}
				    	if (!activitySet.contains(activity)) {
				    		entityManager.persist(activity);
				    		activitySet.add(activity);
				    	}
				    }
				    else {
				    	System.out.println(activity);
				    }

				    transactionTypeSet.add(transactionType);
				}
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		finally {
			if (reader != null) {
				try {
					reader.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
		
		entityManager.getTransaction().commit();
		merrillLynchFile.delete();
	}

	
	private void printTransactionTypeSet() {
		for (String transactionType : transactionTypeSet) {
			System.out.println(transactionType);
		}
	}

	
	public static void main(String[] args) {
		MerrillLynchActivityReader reader = new MerrillLynchActivityReader();
		try {
			File[] merrillLynchFiles = reader.getMerrillLynchActivityFiles();
			for (File merrillLynchFile : merrillLynchFiles) {
				reader.readMerrillLynchActivityFile(merrillLynchFile);
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		System.out.println();
		System.out.println();
		reader.printTransactionTypeSet();
		
		System.exit(0);
	}
}
