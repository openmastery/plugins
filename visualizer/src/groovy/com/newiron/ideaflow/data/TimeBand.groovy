package com.newiron.ideaflow.data


class TimeBand {
    private BandType bandType
    private RelativeTime offset
    int duration
    String comment


    TimeBand(BandType bandType, RelativeTime offset, int duration, String comment = "") {
        this.bandType = bandType
        this.offset = offset
        this.duration = duration
        this.comment = comment
    }
    String getBandType() {
        bandType.name()
    }

    String getOffset() {
        offset.offset
    }

    String getStartTime() {
        offset.time
    }

    String getEndTime() {
        new RelativeTime(offset.offset + duration).shortTime
    }

    String getColor() {
        bandType.color
    }


}
