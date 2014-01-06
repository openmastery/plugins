package com.ideaflow.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(callSuper = true)
class BandStart extends ModelEntity {

	BandType type

	BandStart() {}

	BandStart(BandType type) {
		this.type = type
	}

	String toString() {
		"BandStart: $id, $created, $type"
	}

}
