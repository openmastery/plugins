package com.ideaflow.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(callSuper = true)
class BandStart extends ModelEntry {

    boolean isLinkedToPreviousBand
	BandType type
	String comment

	BandStart() {}

	BandStart(BandType type, String comment, boolean isLinkedToPreviousBand) {
		this.type = type
		this.comment = comment
		this.isLinkedToPreviousBand = isLinkedToPreviousBand
	}

	String toString() {
		"BandStart: $id, $created, $type, $comment"
	}

}
