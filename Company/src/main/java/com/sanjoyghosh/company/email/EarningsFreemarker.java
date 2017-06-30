package com.sanjoyghosh.company.email;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;

import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.PortfolioItemData;
import com.sanjoyghosh.company.db.PortfolioJPA;
import com.sanjoyghosh.company.db.model.Portfolio;
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
			Portfolio portfolio = PortfolioJPA.fetchPortfolio(em, PortfolioJPA.MY_PORTFOLIO_NAME, PortfolioJPA.MY_ALEXA_USER_ID);
			List<PortfolioItemData> items = PortfolioJPA.fetchPortfolioItemDataWithEarnings(em, PortfolioJPA.MY_PORTFOLIO_NAME, 
				PortfolioJPA.MY_ALEXA_USER_ID, startDate, endDate);
			System.out.println(portfolio.getPortfolioItemList().size() + "  " + items.size() + "  " + startDate + "  " + today + "  " + endDate);					
		}
		finally {
			if (em != null) {
				em.close();
			}
		}
	}
	
	
	public static void main(String[] args) {
		EarningsFreemarker freemarker = new EarningsFreemarker();
		freemarker.fetchEarnings();
	}
}
