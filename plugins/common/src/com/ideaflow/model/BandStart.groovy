package com.ideaflow.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class BandStart extends ModelEntity {

	BandType type

	BandStart() {}

	BandStart(BandType type) {
		this.type = type
	}

	String toString() {
		"BandStart: ${created}, ${type}"
	}

}
