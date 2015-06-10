package com.ideaflow.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(callSuper = true)
class StateChange extends ModelEntry {

	StateChangeType type

	StateChange() {}

	StateChange(StateChangeType type) {
		this.type = type
	}

	String toString() {
		"StateChange: $id, $type, $created"
	}

}