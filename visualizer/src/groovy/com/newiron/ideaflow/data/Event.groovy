package com.newiron.ideaflow.data


class Event {
    @Delegate RelativeTime time
    String comment

    Event(int hour, int min, int seconds, String comment) {
        this.time = new RelativeTime(hour, min, seconds)
        this.comment = comment
    }
}
