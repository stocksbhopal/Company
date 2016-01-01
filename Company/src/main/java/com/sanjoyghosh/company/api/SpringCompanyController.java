package com.sanjoyghosh.company.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class SpringCompanyController {

	@RequestMapping("/company")
	public CompanyRest company() {
		return null;
	}
}
