package com.ideaflow.model

import groovy.transform.EqualsAndHashCode


@EqualsAndHashCode
class GenericEvent extends ModelEntity {

	EventType type
	String comment

	GenericEvent() {}

	GenericEvent(EventType type, String comment) {
		this.type = type
		this.comment = comment
	}

	String toString() {
		"GenericEvent: $type, $created, $comment"
	}

}