package com.ideaflow.model.entry

import groovy.transform.EqualsAndHashCode
import org.joda.time.DateTime

@EqualsAndHashCode
abstract class ModelEntry {

	String id
	DateTime created

	ModelEntry() {
		this(new DateTime())
	}

	ModelEntry(DateTime created) {
		this.created = created
	}

}
