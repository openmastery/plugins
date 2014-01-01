package com.ideaflow.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Interval {

    Date created
    String name
    int duration

	Interval() {}

    Interval(Date created, String name, int duration) {
        this.created = created
        this.name = name
        this.duration = duration
    }

    String toString() {
        "Interval: $name : $duration"
    }
}
