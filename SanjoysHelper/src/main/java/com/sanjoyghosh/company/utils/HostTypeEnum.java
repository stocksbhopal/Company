package com.sanjoyghosh.company.utils;

import java.util.HashMap;
import java.util.Map;

public enum HostTypeEnum {

	NONE("None", "none"),
	FINANCE_HELPER("FinanceHelper", "ec2-34-195-18-116.compute-1.amazonaws.com"),
	FINANCE_HELPER_DEV("FinanceHelperDev", "ec2-52-44-163-130.compute-1.amazonaws.com"),
	EARNINGS_UPDATER("EarningsUpdater", "ec2-34-203-121-67.compute-1.amazonaws.com"),
	DEV_BOX("DevBox", "dev-box");
	
	
	private static final Map<String, HostTypeEnum> hostTypeEnumByPublicHostNameMap = new HashMap<>();
	static {
		hostTypeEnumByPublicHostNameMap.put(NONE.publicHostName, NONE);
		hostTypeEnumByPublicHostNameMap.put(FINANCE_HELPER.publicHostName, FINANCE_HELPER);
		hostTypeEnumByPublicHostNameMap.put(FINANCE_HELPER_DEV.publicHostName, FINANCE_HELPER_DEV);
		hostTypeEnumByPublicHostNameMap.put(EARNINGS_UPDATER.publicHostName, EARNINGS_UPDATER);
		hostTypeEnumByPublicHostNameMap.put(DEV_BOX.publicHostName, DEV_BOX);
	}
	
	private String name;
	private String publicHostName;
	
	private HostTypeEnum(String name, String publicHostName) {
		this.name = name;
		this.publicHostName = publicHostName;
	}

	public String getName() {
		return name;
	}

	public String getPublicHostName() {
		return publicHostName;
	}
	
	public static HostTypeEnum getHostTypeEnum(String publicHostName) {
		return hostTypeEnumByPublicHostNameMap.get(publicHostName);
	}
}
