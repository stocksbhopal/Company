package com.sanjoyghosh.company.earnings.intent;

import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;

public class LaunchSanjoysHelper {

	public static SpeechletResponse onLaunch(Session session) throws SpeechletException {
		String text = "Hello, Sanjoy's Helper is always open to help you. " +
			"You can ask her for realtime quotes on listed US stocks. " +
			"You can identify the company by its name, or ticker symbol. " +
			"You can spell the symbol either by letter or using the Wikipedia Spelling Alphabet. " +
			"Just use England for E when using the Spelling Alphabet. " +
			"For example, Alexa, ask Sanjoy's Helper the price of amazon. ";
		
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(text);
		return SpeechletResponse.newTellResponse(outputSpeech);
	}
}
