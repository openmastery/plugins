package com.ideaflow.state;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeConverter {

	public static String toJodaDateTimeString(LocalDateTime dateTime) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd_HHmmss");
		return dateTime.toString(formatter);
	}

	public static LocalDateTime toJodaDateTime(String dateTimeAsString) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd_HHmmss");
		return formatter.parseLocalDateTime(dateTimeAsString);
	}
}
