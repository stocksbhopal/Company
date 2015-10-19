package com.sanjoyghosh.stocks.library.nasdaqcompanylist;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class NasdaqCompanyListReader {

	public void readCompanyList(Reader reader) throws IOException {
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
		for (CSVRecord record : records) {
		    String lastName = record.get("Symbol");
		    String firstName = record.get("Name");
		    String lastSale = record.get("LastSale");
		    String marketCap = record.get("MarketCap");
		    System.out.println(lastName + "   " + firstName + "   " + lastSale + "   " + marketCap);
		}
	}
}
