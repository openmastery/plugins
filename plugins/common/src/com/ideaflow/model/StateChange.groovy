package com.ideaflow.model

import groovy.transform.EqualsAndHashCode


@EqualsAndHashCode
class StateChange extends ModelEntity {

	StateChangeType type

	StateChange() {}

	StateChange(StateChangeType type) {
		this.type = type
	}

	String toString() {
		"StateChange: $type, $created"
	}

}