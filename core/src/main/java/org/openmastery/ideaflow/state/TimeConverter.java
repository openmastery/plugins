package org.openmastery.ideaflow.state;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class TimeConverter {

	// TODO: fix this
	public static String toFormattedDuration(java.time.Duration javaDuration) {
		if (javaDuration == null) {
			return "[undefined]";
		}

		Duration duration = Duration.millis(javaDuration.toMillis());
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
