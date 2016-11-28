package com.sanjoyghosh.company.source.merrilllynch;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.persistence.EntityManager;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.utils.Constants;

public class MerrillLynchHoldingsDynamoDBWriter {
	
    private static final String DYNDB_TABLE_MY_STOCKS = "MyStocks";
    private static final String DYNDB_COL_USER_ID = "userId";
    private static final String DYNDB_COL_SYMBOL = "symbol";
    private static final String DYNDB_COL_FULL_NAME = "fullName";

    private static final String ALEXA_USERID = "amzn1.ask.account.AG3AH7ORTENZGSI5ATVRSNF2V4C2QK6CH3IXLPQMPLAWCCTWZNMGGWOGNVG5E6742XCHBILJRV6IIPQHMBLZ6L7TTTZSBVXRDEC567NDTNJHCJBN5P2JXH3C7XEDD7FSHUGIDIOKG7LTDXPZUU7XGF5VXNDMCKUV7CNL7CI7DVAWKANDCHLHWCJDQYS4VITDDBVTOPJ7FSV2MQQ";
		
	private EntityManager entityManager;
		
	
	public MerrillLynchHoldingsDynamoDBWriter() {
		entityManager = JPAHelper.getEntityManager();
	}
	
	
	private File[] getMerrillLynchHoldingsFiles() {
		File[] merrillLynchFiles = Constants.DownloadsFolder.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().matches(Constants.MerrillLynchHoldingsFileName);
			}
		});
		return merrillLynchFiles;
	}
	
	
	private void readMerrillLynchHoldingsFile(File merrillLynchFile) {
		DynamoDB db = new DynamoDB(new AmazonDynamoDBClient());
		Table myStocks = db.getTable(DYNDB_TABLE_MY_STOCKS);

		Reader reader = null;
		try {
			reader = new FileReader(merrillLynchFile);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
			for (CSVRecord record : records) {
				if (record.size() == 16) {
				    String symbol = record.get("Symbol").trim();
				    Company company = CompanyUtils.fetchCompanyBySymbol(entityManager, symbol);
				    if (company != null) {
						myStocks.putItem(new Item().withPrimaryKey(DYNDB_COL_USER_ID, ALEXA_USERID, DYNDB_COL_SYMBOL, symbol.toLowerCase())
							.withString(DYNDB_COL_FULL_NAME, company.getName()));
				    }
				    else {
				    	System.err.println("No Company for " + symbol);
				    }
				}
			}
		} 
		catch (IOException e) {
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
		
//		merrillLynchFile.delete();
	}
		
	
	public static void main(String[] args) {
		MerrillLynchHoldingsDynamoDBWriter reader = new MerrillLynchHoldingsDynamoDBWriter();
		File[] merrillLynchFiles = reader.getMerrillLynchHoldingsFiles();
		for (File merrillLynchFile : merrillLynchFiles) {
			reader.readMerrillLynchHoldingsFile(merrillLynchFile);
		}
		System.exit(0);
	}
}
