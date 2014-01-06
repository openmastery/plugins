package com.ideaflow.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
abstract class ModelEntity {

	String id
	Date created

	ModelEntity() {
		this(new Date())
	}

	ModelEntity(Date created) {
		this.created = created
	}

}
