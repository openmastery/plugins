package com.ideaflow.model

import groovy.transform.EqualsAndHashCode
import org.joda.time.DateTime

@EqualsAndHashCode(callSuper = true)
class EditorActivity extends ModelEntity {

    String name
    int duration

	EditorActivity() {}

	EditorActivity(DateTime created, String name, int duration) {
		super(created)
        this.name = name
        this.duration = duration
    }

    String toString() {
        "EditorActivity: $id, $created, $name, $duration"
    }

}
