package com.sanjoyghosh.company.earnings.intent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanjoyghosh.company.utils.KeyValuePair;

public class AllSlotValues {

	private String		companyOrSymbol;			// Prefix of the name of the company or the ticker symbol.
	private String		companyOrSymbolSpelt;	// Prefix of the name of the company or the ticker symbol spelt by letter.
	private Double		quantity;
	private LocalDate	localDate;
	
	
	public AllSlotValues() {
	}


	@JsonProperty("cs")
	public String getCompanyOrSymbol() {
		return companyOrSymbol;
	}

	@JsonProperty("cs")
	public void setCompanyOrSymbol(String companyOrSymbol) {
		this.companyOrSymbol = companyOrSymbol;
	}


	@JsonProperty("css")
	public String getCompanyOrSymbolSpelt() {
		return companyOrSymbolSpelt;
	}

	@JsonProperty("css")
	public void setCompanyOrSymbolSpelt(String companyOrSymbolSpelt) {
		this.companyOrSymbolSpelt = companyOrSymbolSpelt;
	}

	
	
	public boolean isEmpty() {
		return (companyOrSymbol == null || companyOrSymbol.length() == 0) && (companyOrSymbolSpelt == null || companyOrSymbolSpelt.length() == 0);
	}

	
	public List<KeyValuePair> toKeyValuePairList() {
		List<KeyValuePair> kvList = new ArrayList<>();
		if (companyOrSymbol != null) {
			KeyValuePair pair = new KeyValuePair("cs", companyOrSymbol);
			kvList.add(pair);
		}
		if (companyOrSymbolSpelt != null) {
			KeyValuePair pair = new KeyValuePair("css", companyOrSymbolSpelt);
			kvList.add(pair);
		}
		if (quantity != null) {
			KeyValuePair pair = new KeyValuePair("q", String.valueOf(quantity));
			kvList.add(pair);
		}
		if (localDate != null) {
			KeyValuePair pair = new KeyValuePair("d", String.valueOf(localDate));
			kvList.add(pair);
		}
		return kvList;
	}
	

	@Override
	public String toString() {
		if (isEmpty()) {
			return "";
		}
		else if ((companyOrSymbol != null && companyOrSymbol.length() > 0) && (companyOrSymbolSpelt != null && companyOrSymbolSpelt.length() > 0)) {
			return companyOrSymbol + " or " + companyOrSymbolSpelt;
		}
		else if (companyOrSymbol != null && companyOrSymbol.length() > 0) {
			return companyOrSymbol;
		}
		else {
			return companyOrSymbolSpelt;
		}
	}


	@JsonProperty("q")
	public Double getQuantity() {
		return quantity;
	}


	@JsonProperty("q")
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}


	@JsonProperty("d")
	public LocalDate getLocalDate() {
		return localDate;
	}


	@JsonProperty("d")
	public void setLocalDate(LocalDate localDate) {
		this.localDate = localDate;
	}
}
