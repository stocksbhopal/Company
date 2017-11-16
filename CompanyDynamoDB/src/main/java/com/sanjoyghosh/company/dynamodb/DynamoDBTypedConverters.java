package com.sanjoyghosh.company.dynamodb;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

public class DynamoDBTypedConverters {
	
	public static class LocalDateConverter implements DynamoDBTypeConverter<String, LocalDate> {

		@Override
		public String convert(LocalDate localDate) {
			return localDate.toString();
		}

		@Override
		public LocalDate unconvert(String localDateStr) {
			try {
				LocalDate localDate = LocalDate.parse(localDateStr, DateTimeFormatter.ISO_DATE);
				return localDate;
			}
			catch (DateTimeException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}
