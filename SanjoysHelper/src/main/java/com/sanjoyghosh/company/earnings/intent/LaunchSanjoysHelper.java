package com.sanjoyghosh.company.earnings.intent;

import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;

public class LaunchSanjoysHelper {

	public static SpeechletResponse onLaunch(Session session) throws SpeechletException {
		String text = "Hello, Finance Helper is always open to help you. " +
			"You can ask her for roughly realtime quotes on listed US stocks. " +
			"You can identify the company by its name, popular name, or ticker symbol. " +
			"A company's official registered name may be different from its popular name. " +
			"For example, Google's registered name is Alphabet Inc. " + 
			"You can spell the name or symbol either by letter or using the Wikipedia Spelling Alphabet. " +
			"Just use England for E when using the Spelling Alphabet. " +
			"For example, you can ask for the price of Apple by saying. " +
			"Alexa, ask Finance Helper the price of alpha papa papa lima england. " + 
			"As a simple example, Alexa, ask Finance Helper, the price of amazon. " +
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
