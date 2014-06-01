package com.newiron.ideaflow.presentation


class DurationDecorationMixin {

	String getHourMinSec() {
		String format = ""
		if (toHours() != "0") {
			format += toHours() + "h "
		}
		if (toMinutes() != "00") {
			format += toMinutes() + "m "
		}
		format += toSeconds() + "s"
		return format
	}

	String toHours() {
		(int) (duration / (60 * 60))
	}

	String toMinutes() {
		int timeWithoutHours = duration % (60 * 60)
		((int) timeWithoutHours / 60).toString().padLeft(2, "0")
	}

	String toSeconds() {
		(duration % 60).toString().padLeft(2, "0")
	}
}
