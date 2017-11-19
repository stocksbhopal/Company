package com.sanjoyghosh.company.alexaskill.intent;

import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;

public class LaunchFinanceHelper {

	public static SpeechletResponse onLaunch(Session session) throws SpeechletException {
		String text = "Hello, Finance Helper is always open to help you. " +
			"You can ask her for roughly realtime quotes on listed US stocks. " +
			"You can identify the company by its name, popular name, or ticker symbol. " +
			"A company's official registered name may be different from its popular name. " +
			"For example, Google's registered name is Alphabet Inc. " + 
			"You can also create your own list of stocks by asking Finance Helper " +
			"to add a number of shares of a particular company, like Amazon. " +
			"You can also modify the number of shares or remove the shares " +
			"from your list.  Of course, you can ask for the complete list as well. " + 
			"Go ahead, try Finance Helper.";
		
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(text);
		
		Reprompt reprompt = new Reprompt();
		PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
		repromptSpeech.setText("Sorry, when you open Finance Helper you can only ask the price of a stock, or say Stop or Cancel or Exit.");
		reprompt.setOutputSpeech(repromptSpeech);

		return SpeechletResponse.newAskResponse(outputSpeech, reprompt);
	}
}
