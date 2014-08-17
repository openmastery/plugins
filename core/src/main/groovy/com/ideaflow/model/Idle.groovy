package com.ideaflow.model

import groovy.transform.EqualsAndHashCode
import org.joda.time.DateTime

@EqualsAndHashCode(callSuper = true)
class Idle extends ModelEntity {

	String comment
	int duration

	Idle() {}

	Idle(DateTime created, String comment, int duration) {
		super(created)
		this.comment = comment
		this.duration = duration
	}

	String toString() {
		"EditorActivity: $id, $created, $duration, ${comment}"
	}
}
