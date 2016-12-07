package com.sanjoyghosh.company.earnings.intent;

import java.util.HashMap;
import java.util.Iterator;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;

public class DynamoDBUtils {
    
	public static Iterator<Item> queryMyStocksByUserId(String userId) {
		DynamoDB db = new DynamoDB(new AmazonDynamoDBClient());
		Table myStocks = db.getTable(InterfaceIntent.DYNDB_TABLE_MY_STOCKS);

		HashMap<String, Object> valueMap = new HashMap<>();
		valueMap.put(":userId", userId);
		QuerySpec querySpec = new QuerySpec()
			.withKeyConditionExpression(InterfaceIntent.DYNDB_COL_USER_ID + " = :userId")
			.withValueMap(valueMap);
				
        ItemCollection<QueryOutcome> items = myStocks.query(querySpec);
        Iterator<Item> iterator = items.iterator();
        return iterator;
	}
}
