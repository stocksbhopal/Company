package com.sanjoyghosh.company.earnings.utils;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JsoupUtils {

	private static final int MAX_RETRIES = 12;
	
	
	public static Document fetchDocument(String url) throws IOException {
		Document doc = null;
		for (int i = 0; i < MAX_RETRIES; i++) {
			try {
				doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0").get();
				return doc;
			}
			catch (IOException e) {
				if (i == (MAX_RETRIES - 1)) {
					throw e;
				}
			}
		}
		return null;
	}
}
