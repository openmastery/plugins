package com.ideaflow.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(callSuper = true)
class StateChange extends ModelEntity {

	StateChangeType type

	StateChange() {}

	StateChange(StateChangeType type) {
		this.type = type
	}

	String toString() {
		"StateChange: $id, $type, $created"
	}

}