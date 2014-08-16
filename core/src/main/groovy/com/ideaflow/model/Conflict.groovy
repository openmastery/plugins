package com.ideaflow.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(callSuper = true)
class Conflict extends ModelEntity {

	String question
	String mistakeType
	String cause
	String notes

	Conflict() {}

	Conflict(String question) {
		this.question = question
	}

	String toString() {
		"Conflict: $id, $created, $question"
	}

}
