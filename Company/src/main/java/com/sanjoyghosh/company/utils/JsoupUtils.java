package com.sanjoyghosh.company.utils;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JsoupUtils {

	private static final int MAX_RETRIES = 12;
	
	
	public static Document fetchDocument(String url) throws IOException {
		Document doc = null;
		for (int i = 0; i < MAX_RETRIES; i++) {
			try {
				doc = Jsoup.connect(url).get();
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
