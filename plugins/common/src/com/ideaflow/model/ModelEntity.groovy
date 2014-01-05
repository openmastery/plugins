package com.ideaflow.model

abstract class ModelEntity {

	Date created

	ModelEntity() {
		this(new Date())
	}

	ModelEntity(Date created) {
		this.created = created
	}

}
