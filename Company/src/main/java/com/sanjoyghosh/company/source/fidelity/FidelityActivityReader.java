package com.sanjoyghosh.company.source.fidelity;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.sanjoyghosh.company.aws.Constants;
import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Activity;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.utils.DateUtils;
import com.sanjoyghosh.company.utils.StringUtils;

public class FidelityActivityReader {
		
	private EntityManager entityManager;
	private Set<String> transactionTypeSet;
	private Map<LocalDate, Set<Activity>> activityByDateMap;
	
	
	public FidelityActivityReader() {
		entityManager = JPAHelper.getEntityManager();
		transactionTypeSet = new HashSet<String>();
		activityByDateMap = new HashMap<LocalDate, Set<Activity>>();
	}
	
	
	private File[] getFidelityActivityFiles() {
		File[] fidelityFiles = Constants.DownloadsFolder.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().startsWith(Constants.FidelityActivityFileNameStart) &&
					   pathname.getName().endsWith(Constants.CSVFileExtension);
			}
		});
		return fidelityFiles;
	}
	
	
	private void readFidelityActivityFile(File fidelityFile) {
		entityManager.getTransaction().begin();

		Reader reader = null;
		try {
			boolean gotRecords = false;
			reader = new FileReader(fidelityFile);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withIgnoreEmptyLines().withHeader().parse(reader);
			for (CSVRecord record : records) {
				if (record.size() == 13) {
					LocalDate tradeDate = null;
					try {
						tradeDate = DateUtils.getLocalDate(record.get("Run Date").trim());
						if (tradeDate == null) {
							continue;
						}
					}
					catch (ParseException e) {
						continue;
					}
					
					LocalDate settledDate = DateUtils.getLocalDate(record.get("Settlement Date").trim());
					settledDate = settledDate == null ? tradeDate : settledDate;
					String account = StringUtils.onlyLast4Characters(record.get("Account").trim());
					String transactionType = record.get("Action").trim();
					transactionType = transactionType.toLowerCase();
					transactionType = transactionType.startsWith("you ") ? transactionType.substring(4) : transactionType;
					transactionType = transactionType.startsWith("merger") ? "merger" : transactionType;
					if (transactionType.length() >= 24) {
						System.err.println(transactionType + "   " + fidelityFile.getName());
						transactionType = transactionType.substring(0, 24);
					}
				    String symbol = record.get("Symbol").trim();
				    String quantityStr = record.get("Quantity");
				    Double quantity = (quantityStr.equals("--") || quantityStr.equals("")) ? null : Double.parseDouble(quantityStr.replaceAll(",", "").trim());
				    String priceStr = record.get("Price ($)");
				    Double price = (priceStr.equals("--") || priceStr.equals("")) ? null : Double.parseDouble(priceStr.replaceAll(",", "").trim());
				    Company company = CompanyUtils.fetchCompanyBySymbol(entityManager, symbol);
				    String amountStr = record.get("Amount ($)");
				    Double amount = (amountStr.equals("--") || amountStr.equals("")) ? null : StringUtils.parseDoubleWithBrackets(amountStr.replaceAll(",", "").trim());
	
				    if (amount == null) {
				    	System.err.println("Amount null for Symbol: " + symbol + ", Transaction Type: " + transactionType + " in file: " + fidelityFile.getName());
				    	continue;
				    }
				    
				    Activity activity = new Activity();
				    activity.setCompanyId(company == null ? 0 : company.getId());
				    activity.setTradeDate(tradeDate);
				    activity.setSettledDate(settledDate);
				    activity.setAccount(account);
				    activity.setBrokerage(Constants.FidelityBrokerage);
				    activity.setSymbol(symbol);
				    activity.setTransactionType(transactionType);
				    activity.setQuantity(quantity);
				    activity.setPrice(price);
				    activity.setAmount(amount);

				    gotRecords = true;
				    if (company != null) {
				    	Set<Activity> activitySet = activityByDateMap.get(settledDate);
				    	if (activitySet == null) {
				    		activitySet = CompanyUtils.fetchAllActivityAtBrokerageForSettledDate(entityManager, Constants.FidelityBrokerage, settledDate);
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

				    System.out.println(activity);
				    transactionTypeSet.add(transactionType);
				}
				else {
					// If got records break to skip the disclaimer text.
					if (gotRecords) {
						break;
					}
				}
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
			return;
		} 
		catch (ParseException e) {
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
		fidelityFile.delete();
	}

	
	private void printTransactionTypeSet() {
		for (String transactionType : transactionTypeSet) {
			System.out.println(transactionType);
		}
	}

	
	public static void main(String[] args) {
		FidelityActivityReader reader = new FidelityActivityReader();
		try {
			File[] fidelityFiles = reader.getFidelityActivityFiles();
			for (File fidelityFile : fidelityFiles) {
				reader.readFidelityActivityFile(fidelityFile);
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
