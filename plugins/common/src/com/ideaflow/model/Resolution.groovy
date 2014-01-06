package com.ideaflow.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(callSuper = true)
class Resolution extends ModelEntity {

	String answer

	Resolution() {}

	Resolution(String answer) {
		this.answer = answer
	}

	String toString() {
		"Conflict: $id, $created, $answer"
	}

}
