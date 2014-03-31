package com.ideaflow.timeline

class Timeline {
    List<ConflictBand> conflictBands = []
    List<TimeBand> timeBands = []
    List<Event> events = []
    List<ActivityDetail> activityDetails = []

    void addTimeBand(TimeBand timeBand) {
        timeBands.add(timeBand)
    }

    void addActivityDetail(ActivityDetail activityDetail) {
        activityDetails.add(activityDetail)
    }

    void addConflictBand(ConflictBand conflictBand) {
        conflictBands.add(conflictBand)
    }

    void addEvent(Event event) {
        events.add(event)
    }
}
