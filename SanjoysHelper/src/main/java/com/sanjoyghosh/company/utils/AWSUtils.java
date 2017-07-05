package com.sanjoyghosh.company.utils;

import java.util.List;

import com.amazonaws.SdkClientException;
import com.amazonaws.util.EC2MetadataUtils;
import com.amazonaws.util.EC2MetadataUtils.NetworkInterface;

public class AWSUtils {

	private static HostTypeEnum hostTypeEnum = null;
	
	public static HostTypeEnum getHostTypeEnum() {
		if (hostTypeEnum == null) {
			try {
				List<NetworkInterface> networks = EC2MetadataUtils.getNetworkInterfaces();
				if (networks.size() > 0) {
					String publicHostName = networks.get(0).getPublicHostname();
					hostTypeEnum = HostTypeEnum.getHostTypeEnum(publicHostName);
				}
			}
			catch (SdkClientException e) {
				hostTypeEnum = HostTypeEnum.DEV_BOX;				
			}
		}
		
		return hostTypeEnum;
	}
	
	
	public static void main(String[] args) {
		System.out.println(getHostTypeEnum().getName());
	}
}
