package com.newiron.ideaflow.data


class RelativeTime {
    int offset

    RelativeTime(int offset) {
        this.offset = offset
    }

    RelativeTime(int hours, int minutes, int seconds) {
        this.offset = (hours * 60 * 60) + (minutes * 60) + seconds
    }

    String getTime() {
        toHours() + ":" + toMinutes() + ":" + toSeconds()
    }

    String getDurationFormattedTime() {
        toHours() + "h " + toMinutes() + "m " + toSeconds() + "s"
    }

    String getShortTime() {
        toHours() + ":" + toMinutes()
    }

    String toHours() {
        (int) (offset / (60 * 60))
    }

    String toMinutes() {
        int timeWithoutHours = offset % (60 * 60)
        ((int) timeWithoutHours / 60).toString().padLeft(2, "0")
    }

    String toSeconds() {
        (offset % 60).toString().padLeft(2, "0")
    }
}
