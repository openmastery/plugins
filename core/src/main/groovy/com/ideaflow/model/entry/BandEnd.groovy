package com.ideaflow.model.entry

import com.ideaflow.model.BandType
import com.ideaflow.model.entry.ModelEntry
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(callSuper = true)
class BandEnd extends ModelEntry {

	BandType type

	BandEnd() {}

	BandEnd(BandType type) {
		this.type = type
	}

	String toString() {
		"BandEnd: $id, $created, $type"
	}

}
