package com.newiron.ideaflow.presentation

import org.joda.time.format.DateTimeFormat

class ClockTimeDecorationMixin {

	String getShortTime() {
		toHours() + ":" + toMinutes()
	}

	String getLongTime() {
		toHours() + ":" + toMinutes() + ":" + toSeconds()
	}

	String getCalendarTime() {
		actualTime.toString(DateTimeFormat.forPattern("hh:mm:ss aa"))
	}

	String getCalendarDate() {
		actualTime.toString(DateTimeFormat.forPattern("MM/dd"))
	}

	String toHours() {
		(int) (relativeOffset / (60 * 60))
	}

	String toMinutes() {
		int timeWithoutHours = relativeOffset % (60 * 60)
		((int) timeWithoutHours / 60).toString().padLeft(2, "0")
	}

	String toSeconds() {
		(relativeOffset % 60).toString().padLeft(2, "0")
	}
}
