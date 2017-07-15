package com.sanjoyghosh.company.earnings.webapp;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EarningsWebAppServlet extends HttpServlet {

	private static final long serialVersionUID = 1179251478396571356L;

	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// super.doGet(req, resp);
		
		resp.setContentType("text/plain");
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentLength("Good Earnings".length());
		resp.getWriter().write("Good Earnings");
		resp.getWriter().flush();
	}
}
