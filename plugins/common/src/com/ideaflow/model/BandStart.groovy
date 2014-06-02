package com.ideaflow.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(callSuper = true)
class BandStart extends ModelEntity {

	BandType type
	String comment

	BandStart() {}

	BandStart(BandType type, String comment) {
		this.type = type
		this.comment = comment
	}

	String toString() {
		"BandStart: $id, $created, $type, $comment"
	}

}
