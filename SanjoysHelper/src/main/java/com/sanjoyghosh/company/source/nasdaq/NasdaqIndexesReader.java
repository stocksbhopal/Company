package com.sanjoyghosh.company.source.nasdaq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NasdaqIndexesReader {
	
    private static final Logger logger = Logger.getLogger(NasdaqIndexesReader.class.getName());


	public static NasdaqIndexes readNasdaqIndexes() {
		String line = null;
		InputStream stream = null;
		BufferedReader reader = null;
		NasdaqIndexes nasdaqIndexes = new NasdaqIndexes();
		try {
			URL url = new URL("http://www.nasdaq.com/aspx/indexrowhtml.aspx?indexname=NASDAQDJIASPXWITHOUTIMAGE");
			URLConnection connection = url.openConnection();
			stream = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(stream));
			line = reader.readLine();
			
			int pos1 = line.indexOf("'");
			int pos2 = line.lastIndexOf("'");
			if (pos1 == -1 || pos2 == -1) {
				logger.log(Level.SEVERE, "Could not extract Indexes from Nasdaq");
				return null;
			}
			line = line.substring(pos1 + 1, pos2);
			
			Document document = Jsoup.parse(line);
			Elements divs = document.select("div[class=indexmkt]");
			for (int i = 0; i < divs.size(); i++) {
				Element div = divs.get(i);

				Elements spans = div.select("span");
				String index = spans.get(0).text().trim();
				int value = (int)Math.round(Double.parseDouble(spans.get(1).text().trim()));
				
				div = div.nextElementSibling();
				spans = div.select("span");
				double changeFactor = spans.get(0).attr("class").equals("green") ? 1.0 : -1.0;
				double change = changeFactor * Double.parseDouble(spans.get(0).text().trim());
				String changePercentStr = spans.get(1).text().trim();
				double changePercent = changeFactor * Double.parseDouble(changePercentStr.substring(2, changePercentStr.length() - 1));
				
				if (index.equals("NASDAQ")) {
					nasdaqIndexes.setIxic(value);
					nasdaqIndexes.setIxicChange(change);
					nasdaqIndexes.setIxicChangePercent(changePercent);
				}
				else if (index.equals("DJIA")) {
					nasdaqIndexes.setDjia(value);
					nasdaqIndexes.setDjiaChange(change);
					nasdaqIndexes.setDjiaChangePercent(changePercent);					
				}
				else if (index.equals("S&P")) {
					nasdaqIndexes.setGspc(value);
					nasdaqIndexes.setGspcChange(change);
					nasdaqIndexes.setGspcChangePercent(changePercent);
				}
			}
		} 
		catch (IOException e) {
			logger.log(Level.SEVERE, "Could not read Indexes from Nasdaq", e);
		}
		finally {
			if (stream != null) {
				try {
					stream.close();
				} 
				catch (IOException e) {
					logger.log(Level.SEVERE, "Cloud not close InputStream from URL", e);
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} 
				catch (IOException e) {
					logger.log(Level.SEVERE, "Could not close Buffered Reader from URL", e);
				}
			}
		}
		
		return nasdaqIndexes;
	}
}
