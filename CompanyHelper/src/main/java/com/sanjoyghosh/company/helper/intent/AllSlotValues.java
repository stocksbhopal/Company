package com.sanjoyghosh.company.helper.intent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sanjoyghosh.company.db.model.Company;
import com.sanjoyghosh.company.utils.KeyValuePair;
import com.sanjoyghosh.company.utils.LocalDateRange;

public class AllSlotValues implements Serializable {

	private static final long serialVersionUID = -9192678317968989774L;

	
	private String			companyOrSymbol;		// Prefix of the name of the company or the ticker symbol.
	private String			companyOrSymbolSpelt;	// Prefix of the name of the company or the ticker symbol spelt by letter.
	
	private Company			company;
	private Double			quantity;
	private LocalDateRange	localDateRange;
	
	
	public AllSlotValues() {}


	public String getCompanyOrSymbol() {
		return companyOrSymbol;
	}

	public void setCompanyOrSymbol(String companyOrSymbol) {
		this.companyOrSymbol = companyOrSymbol;
	}


	public String getCompanyOrSymbolSpelt() {
		return companyOrSymbolSpelt;
	}

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


	public Double getQuantity() {
		return quantity;
	}
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}


	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}


	public LocalDateRange getLocalDateRange() {
		return localDateRange;
	}
	public void setLocalDateRange(LocalDateRange localDateRange) {
		this.localDateRange = localDateRange;
	}
}
