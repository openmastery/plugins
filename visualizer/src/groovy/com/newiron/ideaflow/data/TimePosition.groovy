package com.newiron.ideaflow.data

import org.joda.time.DateTime

class TimePosition extends com.ideaflow.timeline.TimePosition {

	TimePosition(int relativeOffset) {
		// TODO: new DateTime(..) is causing stack overflow when executed w/in grails
		super(new DateTime(relativeOffset), relativeOffset)
	}

    TimePosition(int hours, int minutes, int seconds) {
	    this((hours * 60 * 60) + (minutes * 60) + seconds)
    }

	int getOffset() {
		relativeOffset
	}

    String getLongTime() {
        toHours() + ":" + toMinutes() + ":" + toSeconds()
    }

    String getDurationFormattedTime() {
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

    String getShortTime() {
        toHours() + ":" + toMinutes()
    }

    String toHours() {
        (int) (offset / (60 * 60))
    }

    String toMinutes() {
        int timeWithoutHours = offset % (60 * 60)
        ((int) timeWithoutHours / 60).toString().padLeft(2, "0")
    }

    String toSeconds() {
        (offset % 60).toString().padLeft(2, "0")
    }
}
