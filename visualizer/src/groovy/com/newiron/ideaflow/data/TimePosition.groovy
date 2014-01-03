package com.newiron.ideaflow.data


class TimePosition {
    int offset

    TimePosition(int offset) {
        this.offset = offset
    }

    TimePosition(int hours, int minutes, int seconds) {
        this.offset = (hours * 60 * 60) + (minutes * 60) + seconds
    }

    String getLongTime() {
        toHours() + ":" + toMinutes() + ":" + toSeconds()
    }

    String getDurationFormattedTime() {
        String format = ""
        if (toHours() != "0") {
            format += toHours() + "h "
        }
        format += toMinutes() + "m " + toSeconds() + "s"
        return format
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
