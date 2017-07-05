package com.sanjoyghosh.company.email;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;

import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.PortfolioItemData;
import com.sanjoyghosh.company.db.PortfolioJPA;
import com.sanjoyghosh.company.email.EarningsEmailModel.DateListModel;
import com.sanjoyghosh.company.utils.LocalDateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class EarningsFreemarker {

	private static final Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
	static {
		cfg.setClassForTemplateLoading(EarningsFreemarker.class, "freemarker.templates");
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
		cfg.setLogTemplateExceptions(false);
	}
	
	
	private EarningsEmailModel makeEarningsEmailModel() {
		LocalDate today = LocalDate.now();
		LocalDate startDate = LocalDateUtils.getWeekdayBefore(today, 30);
		LocalDate endDate = LocalDateUtils.getWeekdayAfter(today, 30);
		
		EntityManager em = null;
		EarningsEmailModel model = new EarningsEmailModel();
		try {
			em = JPAHelper.getEntityManager();
			List<PortfolioItemData> items = PortfolioJPA.fetchPortfolioItemDataWithEarnings(em, PortfolioJPA.MY_PORTFOLIO_NAME, 
				PortfolioJPA.MY_ALEXA_USER_ID, startDate, endDate);
			
			LocalDate lastDate = null;
			DateListModel dateListModel = null;
			for (PortfolioItemData item : items) {
				if (lastDate == null || !lastDate.equals(item.getEarningsDate())) {
					lastDate = item.getEarningsDate();
					dateListModel = model.new DateListModel();
					dateListModel.setEarningsDate(item.getEarningsDate());
					model.addDateListModel(dateListModel);
				}
				dateListModel.addPortfolioItemData(item);
			}
			
			return model;
		}
		finally {
			if (em != null) {
				em.close();
			}
		}
	}
	
	
	private void fetchEarnings() {
		EarningsEmailModel model = makeEarningsEmailModel();
		for (DateListModel listModel : model.getEarningsList()) {
			for (PortfolioItemData item : listModel.getItemList()) {
				System.out.println(item.getName());
			}
		}
		System.out.println(model.getEarningsList().size());
		
		try {
			Template template = cfg.getTemplate("EarningsEmailTemplate.html");
			Writer out = new OutputStreamWriter(new FileOutputStream("EarningsEmail.html"));
			template.process(model, out);
			out.close();
		} 
		catch (IOException | TemplateException e) {
			e.printStackTrace();
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
