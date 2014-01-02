package com.newiron.ideaflow.data


class TimeBand {
    private BandType bandType
    @Delegate private RelativeTime position
    int duration

    String getBandType() {
        bandType.name()
    }

}
