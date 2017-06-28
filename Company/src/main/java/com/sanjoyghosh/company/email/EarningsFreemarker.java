package com.sanjoyghosh.company.email;

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
	
	
}
