package com.ideaflow.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(callSuper = true)
class BandEnd extends ModelEntity {

	BandType type

	BandEnd() {}

	BandEnd(BandType type) {
		this.type = type
	}

	String toString() {
		"BandEnd: $id, $created, $type"
	}

}
