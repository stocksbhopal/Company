package com.sanjoyghosh.company.earnings.lambda;

import com.amazon.speech.speechlet.servlet.SpeechletServlet;

public class EarningsServlet extends SpeechletServlet {

	private static final long serialVersionUID = 8530879718768516833L;

	public EarningsServlet() {
		this.setSpeechlet(new EarningsSpeechlet());
	}
}
