package com.ideaflow.timeline

import groovy.transform.EqualsAndHashCode
import org.joda.time.DateTime

@EqualsAndHashCode
class TimePosition {

	int relativeOffset
	DateTime actualTime

	TimePosition(DateTime actualTime, int relativeOffset) {
		this.actualTime = actualTime
		this.relativeOffset = relativeOffset
	}

	String toString() {
		"TimePosition: ${relativeOffset}, ${actualTime}"
	}

}
