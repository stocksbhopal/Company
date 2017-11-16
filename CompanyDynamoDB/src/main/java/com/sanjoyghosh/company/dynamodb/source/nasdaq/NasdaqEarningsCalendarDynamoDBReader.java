package com.sanjoyghosh.company.dynamodb.source.nasdaq;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.sanjoyghosh.company.dynamodb.CompanyDynamoDB;
import com.sanjoyghosh.company.dynamodb.helper.CompanyMatcher;
import com.sanjoyghosh.company.dynamodb.model.Company;
import com.sanjoyghosh.company.dynamodb.model.EarningsDate;
import com.sanjoyghosh.company.utils.JsoupUtils;


public class NasdaqEarningsCalendarDynamoDBReader {

	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
	
	
	private final List<EarningsDate>	earningsDateList = new ArrayList<>();
	
		
	private void readEarningsCalendarFor(LocalDate date) throws IOException {   
		String dateStr = date.format(dateFormatter);
		String yepUrl = "http://www.nasdaq.com/earnings/earnings-calendar.aspx?date=" + dateStr;
		Document doc = null;
		try {
			doc = JsoupUtils.fetchDocument(yepUrl);	
		}
		catch (HttpStatusException e) {
			System.err.println("No doc for: " + yepUrl + ", status: " + e.getStatusCode());
			return;
		}
		
		Elements aElements = doc.select("table[class=USMN_EarningsCalendar").select("a");
	    for (int i = 0; i < aElements.size(); i++) {
		    	Element aElement = aElements.get(i);
		    	String id = aElement.attr("id");
		    	if (id.startsWith("two_column_main_content_CompanyTable_companyname")) {
		    		String amBm = "BM";
		    		Elements prevChildren = aElement.parent().previousElementSibling().children();
		    		if (prevChildren.size() > 0) {
		    			String amBmTitle = prevChildren.get(0).attr("title");
		    			amBm = amBmTitle.equals("Pre-market Quotes") ? "BM" : "AM";
		    		}
		    		
		    		String symbol = aElement.text();
		    		int pos0 = symbol.indexOf("Market Cap:");
		    		int pos1 = symbol.lastIndexOf('(', pos0);
		    		int pos2 = symbol.lastIndexOf(')', pos0);
		    		symbol = symbol.substring(pos1 + 1, pos2);
		    		if ((symbol.indexOf('^') >= 0) || (symbol.indexOf('.') >= 0)) {
		    			continue;
		    		}
		    		Company company = CompanyMatcher.getCompanyBySymbol(symbol);
		    		if (company == null) {
		    			System.out.println("NO COMPANY FOUND FOR: " + aElement.text());
		    			continue;
		    		}
		    			    			    		
		    		EarningsDate earningsDate = new EarningsDate();
		    		earningsDate.setSymbol(symbol);
		    		earningsDate.setDate(date);
		    		earningsDate.setBeforeMarketOrAfterMarket(amBm);	
		    		
		    		earningsDateList.add(earningsDate);
		    }
	    }
    }

	
	private void readEarningsCalendarforMonth() throws IOException {
		LocalDate date = LocalDate.now();
		for (int i = 0; i < 31; i++) {
			readEarningsCalendarFor(date);
			date = date.plusDays(1);
		}		
	}
	
	
	private void writeAllEarningsDates() {
		DynamoDBMapper mapper = CompanyDynamoDB.getDynamoDBMapper();
					
		long startTime = System.currentTimeMillis();
		System.err.println("Before DynamoDB EarningsDate Save");
		List<DynamoDBMapper.FailedBatch> failedList = mapper.batchSave(earningsDateList);
		long endTime = System.currentTimeMillis();
		System.err.println("After DynamoDB EarningsDate Save: " + (endTime - startTime) + " msecs");
		if (failedList.size() > 0) {
			System.err.println("Failed to BatchSave() EarningsDate Records");
			failedList.get(0).getException().printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		NasdaqEarningsCalendarDynamoDBReader reader = new NasdaqEarningsCalendarDynamoDBReader();
		try {
			reader.readEarningsCalendarforMonth();
			reader.writeAllEarningsDates();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}		
		System.exit(0);
	}
}
