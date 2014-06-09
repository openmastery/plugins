package com.newiron.ideaflow.presentation

import com.ideaflow.model.BandType
import com.ideaflow.timeline.AbstractTimeBand
import com.ideaflow.timeline.ActivityDetail
import com.ideaflow.timeline.ConflictBand
import com.ideaflow.timeline.Event
import com.ideaflow.timeline.GenericBand
import com.ideaflow.timeline.TimeBand
import com.ideaflow.timeline.TimeDuration
import com.ideaflow.timeline.TimeEntry
import com.ideaflow.timeline.TimePosition
import com.ideaflow.timeline.Timeline


class TimelineDecorator {

	static void initMixins() {
		TimePosition.mixin(ClockTimeDecorationMixin)
		TimeDuration.mixin(DurationDecorationMixin)
		ActivityDetail.mixin(ActiveBandDecorationMixin)
		Event.mixin(ActiveBandDecorationMixin)
		ConflictBand.mixin(PercentDecorationMixin)
		GenericBand.mixin(PercentDecorationMixin)
	}

	void decorate(Timeline timeline) {
		decorateConflicts(timeline.conflictBands)
		decorateActiveBandTypes(timeline.sequencedTimelineDetail)
		decorateBands(timeline.genericBands, BandType.learning)
		decorateBands(timeline.genericBands, BandType.rework)
	}

	private decorateConflicts(List<ConflictBand> conflictBands) {
		decoratePercents(conflictBands)
	}

	private decorateBands(List<GenericBand> genericBands, BandType bandType) {
		List<TimeBand> bandsMatchingType = genericBands.findAll { it.bandType == bandType }
		decoratePercents(bandsMatchingType)

	}

	private decoratePercents(List<TimeBand> timeBands) {
		Integer totalDuration = timeBands.sum { TimeBand band ->
			band.duration.duration
		}

		if (totalDuration > 0) {
			timeBands.each { TimeBand band ->
				band.percent = 100 * (band.duration.duration / totalDuration)
			}
		}
	}

	private void decorateActiveBandTypes(List<TimeEntry> timeEntries) {
		timeEntries.each { TimeEntry entry ->
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
