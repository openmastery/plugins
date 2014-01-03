package com.newiron.ideaflow.data


class Event {
    @Delegate TimePosition time
    String comment

    Event(int hour, int min, int seconds, String comment) {
        this.time = new TimePosition(hour, min, seconds)
        this.comment = comment
    }
}
