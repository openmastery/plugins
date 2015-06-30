package com.ideaflow.model.entry

import com.ideaflow.model.StateChangeType
import com.ideaflow.model.entry.ModelEntry
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