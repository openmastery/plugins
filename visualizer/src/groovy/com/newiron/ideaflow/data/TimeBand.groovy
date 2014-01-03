package com.newiron.ideaflow.data


class TimeBand {
    private BandType bandType
    private TimePosition startPosition
    private TimePosition endPosition
    String comment


    TimeBand(BandType bandType, TimePosition position, int duration, String comment = "") {
        this.bandType = bandType
        this.startPosition = position
        this.endPosition = new TimePosition(startPosition.offset + duration)
        this.comment = comment
    }

    String getBandType() {
        bandType.name()
    }

    int getOffset() {
        startPosition.offset
    }

    int getDuration() {
        endPosition.offset - startPosition.offset
    }

    String getDurationFormattedTime() {
        TimePosition duration = new TimePosition(duration)
        return duration.getDurationFormattedTime()
    }

    String getStartTime(boolean withSeconds = false) {
        if (withSeconds) {
            return startPosition.longTime
        }
        return startPosition.shortTime
    }

    String getEndTime() {
        endPosition.shortTime
    }

    String getColor() {
        bandType.color
    }


}
