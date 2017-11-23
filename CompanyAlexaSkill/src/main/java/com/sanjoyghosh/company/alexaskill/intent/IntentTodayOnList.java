package com.sanjoyghosh.company.alexaskill.intent;

import java.util.List;

import javax.persistence.EntityManager;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.sanjoyghosh.company.db.JPAHelper;
import com.sanjoyghosh.company.db.PortfolioJPA;
import com.sanjoyghosh.company.dynamodb.model.Portfolio;

public class IntentTodayOnList implements InterfaceIntent {

	public static final int RESULT_SUCCESS = 0;
	public static final int RESULT_ERROR_EXCEPTION = -1;
	public static final int RESULT_ERROR_MISSING_QUANTITY = -2;
	public static final int RESULT_ERROR_BAD_INTENT = -3;

	private static final int DEFAULT_NUM_RESULTS = 6;
	

	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session, IntentResult result)
			throws SpeechletException {
		String intentName = request.getIntent().getName();
		String alexaUserId = session.getUser().getUserId();

		AllSlotValues slotValues = new AllSlotValues();
		boolean hasQuantity = IntentUtils.getQuantity(result);
		int numResults = (int) (hasQuantity ? slotValues.getQuantity() : DEFAULT_NUM_RESULTS);

		EntityManager em = null;
		try {
			em = JPAHelper.getEntityManager();
			if (intentName.equals(InterfaceIntent.INTENT_UPDATE_PRICES_ON_LIST)) {
				return processUpdatePricesOnList(em, alexaUserId, intentName, result);
			}
			if (intentName.equals(InterfaceIntent.INTENT_TODAY_PERFORMANCE)) {
				return processTodayPerformance(em, alexaUserId, intentName, result, -1, false, false);
			}
			if (intentName.equals(InterfaceIntent.INTENT_TODAY_TOP_GAINERS_DOLLARS)) {
				return processTodayPerformance(em, alexaUserId, intentName, result, numResults, true, true);
			}
			if (intentName.equals(InterfaceIntent.INTENT_TODAY_TOP_GAINERS_PERCENTAGE)) {
				return processTodayPerformance(em, alexaUserId, intentName, result, numResults, false, true);
			}
			if (intentName.equals(InterfaceIntent.INTENT_TODAY_TOP_LOSERS_DOLLARS)) {
				return processTodayPerformance(em, alexaUserId, intentName, result, numResults, true, false);
			}
			if (intentName.equals(InterfaceIntent.INTENT_TODAY_TOP_LOSERS_PERCENTAGE)) {
				return processTodayPerformance(em, alexaUserId, intentName, result, numResults, false, false);
			}
		} finally {
			if (em != null) {
				em.close();
			}
		}

		result.setResult(RESULT_ERROR_BAD_INTENT);
		result.setSpeech(false, "Sorry " + getClass().getName() + " does not know what to do with intent: " + request.getIntent().getName());
		return IntentUtils.makeTellResponse(result);
	}


	private SpeechletResponse processUpdatePricesOnList(EntityManager em, String alexaUserId, String intentName, IntentResult result) {
		String speechText = "";

		Portfolio portfolio = PortfolioJPA.fetchPortfolio(em, PortfolioJPA.MY_PORTFOLIO_NAME, alexaUserId);
		if (portfolio == null || portfolio.isEmpty()) {
			speechText = "Sorry, you do not yet have a list of stocks.";
		} 
		else {
			speechText = "Your prices are already up to date. ";
		}

		result.setResponse(String.valueOf(0));
		result.setSpeech(false, speechText);
		return IntentUtils.makeTellResponse(result);
	}


	/**
	 * 
	 * @param em
	 * @param alexaUserId
	 * @param intentName
	 * @param result
	 * @param numResults
	 *            -1 is for TodayPerformance. 1+ for everything else.
	 * @param sortByValueChange
	 *            true for sorting by value change. false for sorting by percent
	 *            change.
	 * @param showGainers
	 *            true for showing gainers. false for showing losers.
	 * @return
	 */
	private SpeechletResponse processTodayPerformance(EntityManager em, String alexaUserId, String intentName,
		IntentResult result, int numResults, boolean sortByValueChange, boolean showGainers) {

		String speechText = "";
		List<PortfolioItemData> portfolioItemDatas = PortfolioJPA.fetchPortfolioItemDataWithPrices(em, PortfolioJPA.MY_PORTFOLIO_NAME, alexaUserId);
		if (portfolioItemDatas == null || portfolioItemDatas.isEmpty()) {
			speechText = "Sorry, you do not yet have a list of stocks.";
		} 
		else {
			int numGainers = 0;
			int numLosers = 0;
			double netValueChange = 0.00D;
			for (PortfolioItemData item : portfolioItemDatas) {
				if (item.getPriceChange() > 0.00D) {
					numGainers++;
				}
				else {
					numLosers++;
				}
				netValueChange += item.getValueChangeDollars();
			}

			speechText = "You have a net "
				+ (netValueChange >= 0.00D ? "gain of " + ((int) netValueChange) : "loss of " + ((int) -netValueChange)) + " dollars. ";
			speechText += "There are " + numGainers + " advancers, and " + numLosers + " decliners. ";

			if (numResults > 0) {
				PortfolioUtils.sortPortfolioItemDataList(portfolioItemDatas, sortByValueChange, !showGainers);

				speechText = "The top " + numResults + (showGainers ? " advancers" : " decliners") + " by "
					+ (sortByValueChange ? "dollars" : "percentage") + " are: ";

				int count = 0;
				for (PortfolioItemData portfolioItemDate : portfolioItemDatas) {
					speechText += (int) portfolioItemDate.getQuantity() + " shares of "
						+ portfolioItemDate.getSpeechName() + ", ";
					if (portfolioItemDate.getValueChangeDollars() >= 0.00) {
						speechText += "gain " + (int) portfolioItemDate.getValueChangeDollars() + " dollars, up "
							+ portfolioItemDate.getPriceChangePercent() + " percent, ";
					} 
					else {
						speechText += "loss " + (int) -portfolioItemDate.getValueChangeDollars() + " dollars, down "
							+ -portfolioItemDate.getPriceChangePercent() + " percent, ";
					}
					count++;
					if (count == numResults) {
						break;
					}
				}
			}
		}

		result.setResponse(String.valueOf(0));
		result.setSpeech(false, speechText);
		return IntentUtils.makeTellResponse(result);
	}
}
