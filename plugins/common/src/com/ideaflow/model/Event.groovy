package com.ideaflow.model

import groovy.transform.EqualsAndHashCode


@EqualsAndHashCode
class Event {
    Date created
    EventType type
    String comment

	Event() {}

    Event(EventType type, String comment) {
        this(type, comment, new Date())
    }

    Event(EventType type, String comment, Date created) {
        this.created = created
        this.type = type
        this.comment = comment
    }

    String toString() {
        "Event: $type, $created, $comment"
    }
}


