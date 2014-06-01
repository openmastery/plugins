package com.newiron.ideaflow.presentation


class ClockTimeDecorationMixin {

	String getShortTime() {
		toHours() + ":" + toMinutes()
	}

	String getLongTime() {
		toHours() + ":" + toMinutes() + ":" + toSeconds()
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
