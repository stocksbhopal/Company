package com.sanjoyghosh.company.dynamodb.helper;

import java.util.HashMap;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.sanjoyghosh.company.dynamodb.CompanyDynamoDB;
import com.sanjoyghosh.company.dynamodb.model.Portfolio;

public class PortfolioMatcher {

	public static List<Portfolio> getPortfolioForAlexaUser(String alexaUserId) {
		HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":alexaUserId", new AttributeValue().withS(alexaUserId));
		DynamoDBQueryExpression<Portfolio> queryExpression = new DynamoDBQueryExpression<Portfolio>()
			.withKeyConditionExpression("AlexaUserId = :alexaUserId")
			.withExpressionAttributeValues(eav);
		List<Portfolio> portfolioList = CompanyDynamoDB.getDynamoDBMapper().query(Portfolio.class, queryExpression);
		return portfolioList;
	}
}
