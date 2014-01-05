package com.ideaflow.model

import groovy.transform.EqualsAndHashCode


@EqualsAndHashCode
class Event extends ModelEntity {

	EventType type
	String comment

	Event() {}

	Event(EventType type, String comment) {
		this.type = type
		this.comment = comment
	}

	String toString() {
		"Event: $type, $created, $comment"
	}

}