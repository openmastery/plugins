package com.ideaflow.model

class Interval {

    Date created
    String name
    int duration

    Interval(Date created, String name, int duration) {
        this.created = created
        this.name = name
        this.duration = duration
    }

    String toString() {
        "Interval: $name : $duration"
    }
}
