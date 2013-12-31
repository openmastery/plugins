package com.ideaflow.model


class Event {
    Date created
    EventType type
    String comment

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


