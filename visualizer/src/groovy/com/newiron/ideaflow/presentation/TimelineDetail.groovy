package com.newiron.ideaflow.presentation

import com.ideaflow.model.BandType
import com.ideaflow.timeline.ActivityDetail
import com.ideaflow.timeline.ConflictBand
import com.ideaflow.timeline.Event
import com.ideaflow.timeline.GenericBand
import com.ideaflow.timeline.TimeEntry
import com.ideaflow.timeline.TimePosition
import com.ideaflow.timeline.Timeline


class TimelineDetail {

	//activity detail has a color
	Timeline timeline

	TimelineDetail(Timeline timeline) {
		this.timeline = timeline
	}

	void initializeActiveBandTypes() {
		timeline.sequencedTimelineDetail.each { TimeEntry entry ->
			handleTimeEntry(entry)
		}
	}

	ConflictBand activeConflict
	GenericBand activeBand

	void handleTimeEntry(ConflictBand conflictBand) {
		activeConflict = conflictBand
	}

	void handleTimeEntry(GenericBand genericBand) {
		activeBand = genericBand
	}

	void handleTimeEntry(ActivityDetail activityDetail) {
		disableEndingBands(activityDetail.time)
		if (activeConflict) {
			activityDetail.activeBandType = activeConflict.bandType
		} else if (activeBand) {
			activityDetail.activeBandType = activeBand.bandType
		}
	}

	void handleTimeEntry(TimeEntry entry) {

	}

	void disableEndingBands(TimePosition time) {
		if (activeConflict?.endPosition?.relativeOffset <= time.relativeOffset) {
			activeConflict = null
		}
		if (activeBand?.endPosition?.relativeOffset <= time.relativeOffset) {
			activeBand = null
		}
	}


}
