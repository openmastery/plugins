package com.ideaflow.model.entry

import com.ideaflow.model.entry.ModelEntry
import groovy.transform.EqualsAndHashCode
import org.joda.time.DateTime

@EqualsAndHashCode(callSuper = true)
class EditorActivity extends ModelEntry {

	String name
	int duration
	boolean modified

	EditorActivity() {}

	EditorActivity(DateTime created, String name, boolean modified, int duration) {
		super(created)
		this.name = name
		this.modified = modified
		this.duration = duration
	}

	String toString() {
		"EditorActivity: $id, $created, ${modified ? "*" : ""}${name}, $duration"
	}

}
