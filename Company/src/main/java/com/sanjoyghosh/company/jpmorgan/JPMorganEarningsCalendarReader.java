package com.sanjoyghosh.company.jpmorgan;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import com.sanjoyghosh.company.db.CompanyUtils;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.StringUtils;
import com.sanjoyghosh.company.model.Company;
import com.sanjoyghosh.company.model.EarningsDate;
import com.sanjoyghosh.company.yahoo.YahooEarningsCalendarReader;

public class JPMorganEarningsCalendarReader {
	
	private EntityManager entityManager;
	private SimpleDateFormat dateParser = new SimpleDateFormat("dd-MMM-yy");
	private List<String> opinionList = Arrays.asList("US Overweight", "US Neutral", "US Underweight", "US Not Rated");
	
	
	public JPMorganEarningsCalendarReader() {}
	
	
	private void readJPMEarningsCalendar(String earningsCalendar) throws IOException {
		String line = null;
		LineNumberReader reader = new LineNumberReader(new StringReader(earningsCalendar));
		while ((line = reader.readLine()) != null) {
			Date earningsDate = null;
			try {
				earningsDate = dateParser.parse(line);
			} 
			catch (ParseException e) {
			}
			if (earningsDate != null && line.length() > 24) {
				String[] prefixAndSuffix = StringUtils.prefixAndSuffixWithEmbedded(line, opinionList);
				if (prefixAndSuffix != null) {
					String prefix = prefixAndSuffix[0];
					String embedded = prefixAndSuffix[1];
					String suffix = prefixAndSuffix[2];
					
					int pos = prefix.lastIndexOf(' ');
					String symbol = prefix.substring(pos, prefix.length()).trim();
					
					pos = embedded.indexOf(' ');
					String opinion = embedded.substring(pos, embedded.length()).trim();
					
					pos = suffix.indexOf('$');
					String analyst = suffix.substring(0, pos).trim();
							
					EarningsDate earningsDateDB = CompanyUtils.fetchEarningsDateForSymbolDate(entityManager, symbol, new Timestamp(earningsDate.getTime()));
					if (earningsDateDB != null) {
						earningsDateDB.setJpmAnalyst(analyst);
						earningsDateDB.setJpmOpinion(opinion);
						entityManager.persist(earningsDateDB);
					}
					else {
						if (earningsDate.after(new Date())) {
							Company company = CompanyUtils.fetchCompanyBySymbol(entityManager, symbol);
							earningsDateDB = new EarningsDate();
							earningsDateDB.setBeforeMarketOrAfterMarket("BM");
							earningsDateDB.setCompanyId(company == null ? null : company.getId());
							earningsDateDB.setEarningsDate(new Timestamp(earningsDate.getTime()));
							earningsDateDB.setJpmAnalyst(analyst);
							earningsDateDB.setJpmOpinion(opinion);
							earningsDateDB.setSymbol(symbol);
							
							YahooEarningsCalendarReader.readAnalystOpinionYahoo(earningsDateDB);
							YahooEarningsCalendarReader.readSummaryYahoo(earningsDateDB);
							entityManager.persist(earningsDateDB);
							System.out.println("MISSING: " + symbol + "  " + earningsDate);
						}
					}
				}
			}
		}
	}
	
	
	private void readJPMEarningsCalendarFile(File jpmEarningsFile) throws IOException {
		InputStream stream = null;
		PDFParser parser = null;
		COSDocument cosDocument = null;
		PDDocument pdDocument = null;
		PDFTextStripper textStripper = null;
		try {
			stream = new FileInputStream(jpmEarningsFile);
			parser = new PDFParser(new FileInputStream(jpmEarningsFile));
			parser.parse();
			cosDocument = parser.getDocument();
			pdDocument = new PDDocument(cosDocument);
			textStripper = new PDFTextStripper();
			textStripper.setStartPage(1);
			textStripper.setEndPage(pdDocument.getNumberOfPages());
			String text = textStripper.getText(pdDocument);
			readJPMEarningsCalendar(text);
		}
		finally {
			if (pdDocument != null) {
				pdDocument.close();
			}
			if (cosDocument != null) {
				cosDocument.close();
			}
			if (parser != null) {
				parser.clearResources();
			}
			if (stream != null) {
				stream.close();
			}
		}
	}
	
	
	private void readAllJPMorganEarningsCalendarFiles() {
		entityManager = JPAHelper.getEntityManager();
		
		File jpmEarningsFolder = new File("/Users/sanjoyghosh/Downloads/JPMEarnings");
		for (File jpmEarningsFile : jpmEarningsFolder.listFiles()) {
			entityManager.getTransaction().begin();
			try {
				readJPMEarningsCalendarFile(jpmEarningsFile);
				entityManager.getTransaction().commit();
			} 
			catch (IOException e) {
				e.printStackTrace();
				entityManager.getTransaction().rollback();
			}
		}
	}
	
	
	public static void main(String[] args) {
		JPMorganEarningsCalendarReader reader = new JPMorganEarningsCalendarReader();
		reader.readAllJPMorganEarningsCalendarFiles();
		System.exit(0);
	}
}
