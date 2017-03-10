package com.sanjoyghosh.company.earnings.intent;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.sanjoyghosh.company.db.PortfolioItemData;
import com.sanjoyghosh.company.db.PortfolioJPA;
import com.sanjoyghosh.company.source.nasdaq.NasdaqRealtimeQuote;
import com.sanjoyghosh.company.source.nasdaq.NasdaqRealtimeQuoteReader;
import com.sanjoyghosh.company.utils.LocalDateRange;
import com.sanjoyghosh.company.utils.LoggerUtils;

public class IntentGetMyStocksWithEarnings implements InterfaceIntent {

    private static final Logger logger = Logger.getLogger(IntentGetMyStocksWithEarnings.class.getName());

    
    private double getNetValueChange(List<PortfolioItemData> portfolioItemDataList) {
    	double valueChange = 0.00;
		for (PortfolioItemData portfolioItemData : portfolioItemDataList) {
			NasdaqRealtimeQuote quote = null;
			try {
				quote = NasdaqRealtimeQuoteReader.fetchNasdaqStockSummary(portfolioItemData.getSymbol());
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			if (quote != null) {
				portfolioItemData.setPriceChangePercent(quote.getPriceChangePercent());
				portfolioItemData.setValueChangeDollars(portfolioItemData.getQuantity() * quote.getPriceChange());
				valueChange += portfolioItemData.getValueChangeDollars();
			}
		}
		return valueChange;
    }
    
    
    private SpeechletResponse respondWithEarningsInfo(IntentRequest request, Session session, LocalDateRange dateRange) {
		String speech = "";
		List<PortfolioItemData> portfolioItemDataList = PortfolioJPA.fetchPortfolioItemDataWithEarnings(
			PortfolioJPA.MY_PORTFOLIO_NAME, session.getUser().getUserId(), dateRange.getStartDate(), dateRange.getEndDate());
		if (portfolioItemDataList == null || portfolioItemDataList.size() == 0) {
			speech = "Sorry you don't have any stocks with earnings " + dateRange.toAlexaString() + ".";
		}
		else {
			int length = portfolioItemDataList.size();
			speech = "You have " + length + (length == 1 ? " stock " : " stocks ") + "with earnings " + dateRange.toAlexaString() + ". ";
			int valueChange = (int)getNetValueChange(portfolioItemDataList);
			speech += "For a net " + (valueChange >= 0 ? "gain" : "loss") + " of " + valueChange + " dollars. ";
			
			Collections.sort(portfolioItemDataList, new Comparator<PortfolioItemData>() {
				@Override
				public int compare(PortfolioItemData o1, PortfolioItemData o2) {
					return -(new Double(o1.getValueChangeDollars()).compareTo(o2.getValueChangeDollars()));
				}
			});
			for (PortfolioItemData portfolioItemData : portfolioItemDataList) {
				speech += (int)portfolioItemData.getQuantity() + " shares of " + portfolioItemData.getSpeechName() + ", ";
				if (portfolioItemData.getValueChangeDollars() >= 0.00) {
					speech += "gain " + (int)portfolioItemData.getValueChangeDollars() + " dollars, up " + portfolioItemData.getPriceChangePercent() + " percent. ";
				}
				else {
					speech += "loss " + (int)(-portfolioItemData.getValueChangeDollars()) + " dollars, down " + -portfolioItemData.getPriceChangePercent() + " percent. ";					
				}
			}
		}
		logger.info(LoggerUtils.makeLogString(session, speech));
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(speech);
		return SpeechletResponse.newTellResponse(outputSpeech);
    }
    
    
    private SpeechletResponse respondToInvalidTimeFrame(IntentRequest request, Session session) {
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText("Need a time frame less than 31 days ahead, like today or next week.");

		Reprompt reprompt = new Reprompt();
		PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
		repromptSpeech.setText("Sorry, need a time frame less than 31 days ahead, like today or next week.");
		reprompt.setOutputSpeech(repromptSpeech);
		
		logger.info(LoggerUtils.makeLogString(session, request.getIntent().getName() + " user did not provide a proper time frame."));
		return SpeechletResponse.newAskResponse(outputSpeech, reprompt);	    				    	
    }
    
    
	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		SpeechletResponse response = null;
		LocalDateRange dateRange = IntentUtils.getValidDateRange(request);
		if (dateRange == null) {
			response = respondToInvalidTimeFrame(request, session);
		}
		else {
			response = respondWithEarningsInfo(request, session, dateRange);
		}
		return response;
	}
}
