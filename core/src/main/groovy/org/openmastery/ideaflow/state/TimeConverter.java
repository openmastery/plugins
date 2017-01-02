package org.openmastery.ideaflow.state;

import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class TimeConverter {

	public static String toJodaDateTimeString(LocalDateTime dateTime) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd_HHmmss");
		return dateTime.toString(formatter);
	}

	public static LocalDateTime toJodaDateTime(String dateTimeAsString) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd_HHmmss");
		return formatter.parseLocalDateTime(dateTimeAsString);
	}

	public static String toFormattedDuration(Duration duration) {
		if (duration == null) {
			return "[undefined]";
		}

		PeriodFormatterBuilder builder = new PeriodFormatterBuilder()
				.appendDays()
				.appendSuffix("d")
				.appendHours()
				.appendSuffix("h")
				.appendMinutes()
				.appendSuffix("m");

				if (duration.getStandardSeconds() < 60 * 60) {
					builder.appendSeconds()
							.appendSuffix("s");
				}

		PeriodFormatter formatter = builder.toFormatter();

		return formatter.print(duration.toPeriod());
	}
}
