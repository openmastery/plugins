package com.newiron.ideaflow.presentation

import com.ideaflow.model.BandType
import com.ideaflow.timeline.ActivityDetail
import com.ideaflow.timeline.ConflictBand
import com.ideaflow.timeline.Event
import com.ideaflow.timeline.GenericBand
import com.ideaflow.timeline.TimeBand
import com.ideaflow.timeline.TimeEntry
import com.ideaflow.timeline.TimePosition
import com.ideaflow.timeline.Timeline


class TimelineDetail {

	//activity detail has a color
	Timeline timeline

	TimelineDetail(Timeline timeline) {
		this.timeline = timeline
		initializeActiveBandTypes()
	}

	List<TimeEntry> listRows() {
		timeline.sequencedTimelineDetail
	}

	private void initializeActiveBandTypes() {
		timeline.sequencedTimelineDetail.each { TimeEntry entry ->
			handleTimeEntry(entry)
		}
	}

	private ConflictBand activeConflict
	private GenericBand activeBand

	private void handleTimeEntry(ConflictBand conflictBand) {
		activeConflict = conflictBand
	}

	private void handleTimeEntry(GenericBand genericBand) {
		activeBand = genericBand
	}

	private void handleTimeEntry(ActivityDetail activityDetail) {
		disableEndingBands(activityDetail.time)
		decorateWithActiveBand(activityDetail)
	}

	private void handleTimeEntry(Event event) {
		decorateWithActiveBand(event)
	}

	private decorateWithActiveBand(TimeEntry entry) {
		if (activeConflict) {
			entry.activeBandType = activeConflict.bandType
		} else if (activeBand) {
			entry.activeBandType = activeBand.bandType
		}
	}

	private void disableEndingBands(TimePosition time) {
		if (activeConflict?.endPosition?.relativeOffset <= time.relativeOffset) {
			activeConflict = null
		}
		if (activeBand?.endPosition?.relativeOffset <= time.relativeOffset) {
			activeBand = null
		}
	}


}
