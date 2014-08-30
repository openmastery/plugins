package com.ideaflow.timeline

import groovy.transform.EqualsAndHashCode
import org.joda.time.DateTime

@EqualsAndHashCode
class TimePosition {

	final int relativeOffset
	final DateTime actualTime

	TimePosition(DateTime actualTime, int relativeOffset) {
		this.actualTime = actualTime
		this.relativeOffset = relativeOffset
	}

	TimePosition(int hours, int minutes, int seconds) {
		this(DateTime.now(), (hours * 60 * 60) + (minutes * 60) + seconds)
	}

	boolean isBefore(TimePosition position) {
		actualTime.isBefore(position.actualTime)
	}

	boolean isAfter(TimePosition position) {
		actualTime.isAfter(position.actualTime)
	}

	boolean isEqual(TimePosition position) {
		actualTime.isEqual(position.actualTime)
	}

	String toString() {
		"TimePosition: ${relativeOffset}, ${actualTime}"
	}

}
