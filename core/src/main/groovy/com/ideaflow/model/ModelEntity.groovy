package com.ideaflow.model

import groovy.transform.EqualsAndHashCode
import org.joda.time.DateTime

@EqualsAndHashCode
abstract class ModelEntity {

	String id
	DateTime created

	ModelEntity() {
		this(new DateTime())
	}

	ModelEntity(DateTime created) {
		this.created = created
	}

}
