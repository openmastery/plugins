package com.ideaflow.model

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