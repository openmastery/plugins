package com.ideaflow.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Conflict extends Event {

	String question
	String mistakeType
	String cause
	String notes

	Conflict() {}

	Conflict(String question) {
		this.question = question
	}

	String toString() {
		"Conflict: $created, $question"
	}

}
