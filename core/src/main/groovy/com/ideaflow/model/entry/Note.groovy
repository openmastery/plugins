package com.ideaflow.model.entry

import com.ideaflow.model.entry.ModelEntry
import groovy.transform.EqualsAndHashCode


@EqualsAndHashCode(callSuper = true)
class Note extends ModelEntry {

	String comment

	Note() {}

	Note(String comment) {
		this.comment = comment
	}

	String toString() {
		"Note: $id, $created, $comment"
	}

}