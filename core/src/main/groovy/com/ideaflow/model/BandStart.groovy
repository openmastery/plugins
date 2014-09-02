package com.ideaflow.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(callSuper = true)
class BandStart extends ModelEntity {

	BandType type
	String comment
	boolean isLinkedToParentConflict

	BandStart() {}

	BandStart(BandType type, String comment, boolean isLinkedToParentConflict) {
		this.type = type
		this.comment = comment
		this.isLinkedToParentConflict = isLinkedToParentConflict
	}

	String toString() {
		"BandStart: $id, $created, $type, $comment"
	}

}
