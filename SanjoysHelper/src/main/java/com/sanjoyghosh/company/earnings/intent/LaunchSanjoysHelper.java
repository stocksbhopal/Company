package com.sanjoyghosh.company.earnings.intent;

import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;

public class LaunchSanjoysHelper {

	public static SpeechletResponse onLaunch(Session session) throws SpeechletException {
		String text = "Hello, Sanjoy's Helper is always open to help you. " +
			"You can ask her for roughly realtime quotes on listed US stocks. " +
			"You can identify the company by its name, or ticker symbol. " +
			"You can spell the symbol either by letter or using the Wikipedia Spelling Alphabet. " +
			"Just use England for E when using the Spelling Alphabet. " +
			"For example, Alexa, ask Sanjoy's Helper, the price of amazon. " +
			"Go ahead, ask the price of your favorite stock.";
		
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(text);
		
		Reprompt reprompt = new Reprompt();
		PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
		repromptSpeech.setText("Sorry, ask the price of a stock, or say Stop or Cancel or Exit.");
		reprompt.setOutputSpeech(repromptSpeech);

		return SpeechletResponse.newAskResponse(outputSpeech, reprompt);
	}
}
