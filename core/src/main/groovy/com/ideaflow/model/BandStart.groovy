package com.ideaflow.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(callSuper = true)
class BandStart extends ModelEntity {

	BandType type
	String comment
	boolean isContainer
	boolean isLinkedToPreviousConflict

	BandStart() {}

	BandStart(BandType type, String comment, boolean isContainer, boolean isLinkedToPreviousConflict) {
		this.type = type
		this.comment = comment
		this.isContainer = isContainer
		this.isLinkedToPreviousConflict = isLinkedToPreviousConflict
	}

	String toString() {
		"BandStart: $id, $created, $type, $comment"
	}

}
