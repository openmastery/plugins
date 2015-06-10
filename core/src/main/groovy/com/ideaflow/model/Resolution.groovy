package com.ideaflow.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(callSuper = true)
class Resolution extends ModelEntry {

	String answer

	Resolution() {}

	Resolution(String answer) {
		this.answer = answer
	}

	String toString() {
		"Resolution: $id, $created, $answer"
	}

}
