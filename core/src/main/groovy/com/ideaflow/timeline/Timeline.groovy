package com.ideaflow.timeline

class Timeline {
	List<ConflictBand> conflictBands = []
	List<GenericBand> genericBands = []
	List<Event> events = []
	List<ActivityDetail> activityDetails = []

	void addGenericBand(GenericBand timeBand) {
		genericBands.add(timeBand)
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

	List<TimeBand> getTimeBands() {
		(conflictBands + genericBands).sort {
			TimeBand timeBand ->
				timeBand.startPosition.relativeOffset
		}
	}

	List<TimeEntry> getSequencedTimelineDetail() {
		(conflictBands + genericBands + events + activityDetails).sort {
			TimeEntry timeEntry ->
				timeEntry.time.relativeOffset
		}
	}



}
