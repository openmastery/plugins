package com.ideaflow.model.entry

import com.ideaflow.model.entry.ModelEntry
import groovy.transform.EqualsAndHashCode
import org.joda.time.DateTime

@EqualsAndHashCode(callSuper = true)
class Idle extends ModelEntry {

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
