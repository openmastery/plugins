package com.newiron.ideaflow.data


class Conflict {
    String conflict
    String resolution
    String cause
    String mistakeType //this is from a set of loaded types
    String notes

    @Delegate TimeBand timeBand

    Conflict(String conflict, String resolution, TimePosition startTime, int duration) {
        this.conflict = conflict
        this.resolution = resolution
        this.timeBand = new TimeBand(BandType.Conflict, startTime, duration, conflict)
    }
}
