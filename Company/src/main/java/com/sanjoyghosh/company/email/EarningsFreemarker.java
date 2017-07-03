package com.sanjoyghosh.company.email;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.util.EC2MetadataUtils;
import com.amazonaws.util.EC2MetadataUtils.NetworkInterface;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.PortfolioItemData;
import com.sanjoyghosh.company.db.PortfolioJPA;
import com.sanjoyghosh.company.utils.LocalDateUtils;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class EarningsFreemarker {

	private static final Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
	static {
		cfg.setClassForTemplateLoading(EarningsFreemarker.class, "freemarker.templates");
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
		cfg.setLogTemplateExceptions(false);
	}
	
	
	private void fetchEarnings() {
		LocalDate today = LocalDate.now();
		LocalDate startDate = LocalDateUtils.getWeekdayBefore(today, 30);
		LocalDate endDate = LocalDateUtils.getWeekdayAfter(today, 30);
		
		EntityManager em = null;
		try {
			em = JPAHelper.getEntityManager();
//			Portfolio portfolio = PortfolioJPA.fetchPortfolio(em, PortfolioJPA.MY_PORTFOLIO_NAME, PortfolioJPA.MY_ALEXA_USER_ID);
			List<PortfolioItemData> items = PortfolioJPA.fetchPortfolioItemDataWithEarnings(em, PortfolioJPA.MY_PORTFOLIO_NAME, 
				PortfolioJPA.MY_ALEXA_USER_ID, startDate, endDate);
			System.out.println(items.size() + "  " + startDate + "  " + today + "  " + endDate);					
		}
		finally {
			if (em != null) {
				em.close();
			}
		}
	}
	
	
	public static void main(String[] args) {
		EarningsFreemarker freemarker = new EarningsFreemarker();
		try {
			List<NetworkInterface> networks = EC2MetadataUtils.getNetworkInterfaces();
			for (NetworkInterface network: networks) {
				System.out.println(network.getHostname() + "  " + network.getPublicHostname());
			}
			System.out.println(EC2MetadataUtils.getLocalHostName());
			System.out.println(new Instance().getPublicDnsName());
			System.out.println(new Instance().getKeyName());
			System.out.println(Inet4Address.getLocalHost().getHostAddress());
			System.out.println(InetAddress.getLocalHost().getHostName());
			freemarker.fetchEarnings();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}
