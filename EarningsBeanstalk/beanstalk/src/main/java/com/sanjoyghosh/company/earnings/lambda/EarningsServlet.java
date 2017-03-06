package com.sanjoyghosh.company.earnings.lambda;

import javax.servlet.ServletException;

import com.amazon.speech.speechlet.servlet.SpeechletServlet;

public class EarningsServlet extends SpeechletServlet {

	private static final long serialVersionUID = 8530879718768516833L;

	public EarningsServlet() {
		this.setSpeechlet(new EarningsSpeechlet());
	}

	@Override
	public void init() throws ServletException {
		EarningsSpeechlet.init();
	}
}
