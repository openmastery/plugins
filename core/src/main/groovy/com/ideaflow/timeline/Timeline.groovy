package com.ideaflow.timeline

class Timeline {
	List<ConflictBand> conflictBands = []
	List<GenericBand> genericBands = []
	List<Event> events = []
	List<ActivityDetail> activityDetails = []
	List<IdleDetail> idleDetails = []

	void addGenericBand(GenericBand timeBand) {
		genericBands.add(timeBand)
	}

	void addActivityDetail(ActivityDetail activityDetail) {
		activityDetails.add(activityDetail)
	}

	void addIdleDetail(IdleDetail idleDetail) {
		idleDetails.add(idleDetail)
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
		(conflictBands + genericBands + events + activityDetails + idleDetails).sort {
			TimeEntry timeEntry ->
				timeEntry.time.relativeOffset
		}
	}

	List<TimeEntry> getSequencedTimeline() {
		(conflictBands + genericBands + events).sort {
			TimeEntry timeEntry ->
				timeEntry.time.relativeOffset
		}
	}

}
