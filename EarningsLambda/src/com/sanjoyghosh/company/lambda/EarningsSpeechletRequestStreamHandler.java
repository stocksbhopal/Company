package com.sanjoyghosh.company.lambda;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

public class EarningsSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {

    private static final Set<String> supportedApplicationIds = new HashSet<String>();
    static {
        /*
         * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit" the relevant
         * Alexa Skill and put the relevant Application Ids in this Set.
         */
        supportedApplicationIds.add("amzn1.ask.skill.8321fccf-fece-461b-bb3a-51d61aa139a9");
    }

    public EarningsSpeechletRequestStreamHandler() {
        super(new EarningsSpeechlet(), supportedApplicationIds);
    }
}
