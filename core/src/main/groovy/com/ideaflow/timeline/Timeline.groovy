package com.ideaflow.timeline

import com.ideaflow.model.Conflict

class Timeline {
	List<ConflictBand> conflictBands = []
	List<GenericBand> genericBands = []
	List<TimeBandContainer> timeBandContainers = []
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

	void addTimeBandContainer(TimeBandContainer timeBandContainer) {
		timeBandContainers.add(timeBandContainer)
	}

	List<TimeBand> getTimeBands() {
		sortTimeBands(conflictBands + genericBands + timeBandContainers)
	}

	List<TimeEntry> getSequencedTimelineDetail() {
		sortTimeEntries(conflictBands + genericBands + events + activityDetails + idleDetails)
	}

	List<TimeEntry> getSequencedTimeline() {
		sortTimeEntries(conflictBands + genericBands + timeBandContainers + events)
	}

	private List<TimeBand> sortTimeBands(List<TimeBand> timeBands) {
		timeBands.sort {
			TimeBand timeBand ->
				timeBand.startPosition.relativeOffset
		}
	}

	private List<TimeEntry> sortTimeEntries(List<TimeEntry> timeEntries) {
		timeEntries.sort {
			TimeEntry timeEntry ->
				timeEntry.time.relativeOffset
		}
	}

}
