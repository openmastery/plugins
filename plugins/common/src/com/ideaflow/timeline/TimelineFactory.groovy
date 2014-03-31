package com.ideaflow.timeline

import com.ideaflow.model.BandEnd
import com.ideaflow.model.BandStart
import com.ideaflow.model.BandType
import com.ideaflow.model.Conflict
import com.ideaflow.model.EditorActivity
import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.ModelEntity
import com.ideaflow.model.Note
import com.ideaflow.model.Resolution
import com.ideaflow.model.StateChange

class TimelineFactory {

	Timeline timeline

	Timeline create(IdeaFlowModel ifm) {
		TimelineBuilder builder = new TimelineBuilder()
		ifm.entityList.each { ModelEntity entity ->
			builder.addEntity(entity)
		}
		return builder.timeline
	}

	private static class TimelineBuilder {
		Timeline timeline = new Timeline()
		Map<BandType, TimeBand> activeTimeBands = [:]
		ConflictBand activeConflictBand
		int relativeTime = 0

		void addEntity(BandStart bandStart) {
			// what if band is already started???
			TimeBand timeBand = new TimeBand(relativeTime)
			timeBand.bandStart = bandStart
			activeTimeBands.put(bandStart.type, timeBand)
		}

		void addEntity(BandEnd bandEnd) {
			TimeBand timeBand = activeTimeBands.remove(bandEnd.type)

			if (timeBand != null) {
				timeBand.duration = relativeTime - timeBand.offset
				timeBand.bandEnd = bandEnd
				timeline.addTimeBand(timeBand)
			} else {
				// expode???
			}
		}

		void addEntity(EditorActivity editorActivity) {
			ActivityDetail activityDetail = new ActivityDetail(relativeTime)
			activityDetail.editorActivity = editorActivity
			timeline.addActivityDetail(activityDetail)

			relativeTime += editorActivity.duration
		}

		void addEntity(Conflict conflict) {
			//if already set, exploded!!
			activeConflictBand = new ConflictBand(relativeTime)
			activeConflictBand.conflict = conflict
		}

		void addEntity(Resolution resolution) {
			//if no active conflict, explode!!
			activeConflictBand.resolution = resolution
			activeConflictBand.duration = relativeTime - activeConflictBand.offset
			timeline.addConflictBand(activeConflictBand)
			activeConflictBand = null
		}

		void addEntity(Note note) {
			timeline.addEvent(new Event(relativeTime, note))
		}

		void addEntity(StateChange stateChange) {
		}

		void addEntity(ModelEntity unknown) {
			throw new RuntimeException("Unknown ModelEntity type ${unknown?.class}")
		}

	}


}
