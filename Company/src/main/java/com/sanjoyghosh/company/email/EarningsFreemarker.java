package com.sanjoyghosh.company.email;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;

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
		LocalDate startDate = LocalDateUtils.getWeekdayBefore(today, 0);
		LocalDate endDate = LocalDateUtils.getWeekdayAfter(today, 15);
		
		EntityManager em = null;
		try {
			em = JPAHelper.getEntityManager();
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
			freemarker.fetchEarnings();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}
