package com.ideaflow.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(callSuper = true)
class BandStart extends ModelEntity {

	BandType type
	String comment
	boolean isLinkedToPreviousConflict

	BandStart() {}

	BandStart(BandType type, String comment, boolean isLinkedToPreviousConflict) {
		this.type = type
		this.comment = comment
		this.isLinkedToPreviousConflict = isLinkedToPreviousConflict
	}

	String toString() {
		"BandStart: $id, $created, $type, $comment"
	}

}
