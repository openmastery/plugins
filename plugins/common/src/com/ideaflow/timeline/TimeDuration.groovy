package com.ideaflow.timeline

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class TimeDuration {

	int duration

	TimeDuration(int duration) {
		this.duration = duration
	}

	String toString() {
		"TimeDuration: ${duration}"
	}
}
