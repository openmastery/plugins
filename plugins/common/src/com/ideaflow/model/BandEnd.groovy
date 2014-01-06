package com.ideaflow.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class BandEnd extends ModelEntity {

	BandType type

	BandEnd() {}

	BandEnd(BandType type) {
		this.type = type
	}

	String toString() {
		"BandEnd: ${created}, ${type}"
	}

}
