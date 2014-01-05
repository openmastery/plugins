package com.ideaflow.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class EditorActivity extends ModelEntity {

    String name
    int duration

	EditorActivity() {}

	EditorActivity(Date created, String name, int duration) {
		super(created)
        this.name = name
        this.duration = duration
    }

    String toString() {
        "EditorActivity: $name : $duration"
    }

}
