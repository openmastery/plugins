package com.ideaflow.timeline

import com.ideaflow.model.BandEnd
import com.ideaflow.model.BandStart
import com.ideaflow.model.BandType
import com.ideaflow.model.Conflict
import com.ideaflow.model.EditorActivity
import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.Idle
import com.ideaflow.model.ModelEntity
import com.ideaflow.model.Note
import com.ideaflow.model.Resolution
import com.ideaflow.model.StateChange
import org.joda.time.DateTime

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
		Map<BandType, GenericBand> activeGenericBands = [:]
		ConflictBand activeConflictBand
		int relativeTime = 0

		void addEntity(BandStart bandStart) {
			// what if band is already started???
			GenericBand genericBand = new GenericBand()
			genericBand.bandStart = bandStart
			genericBand.startPosition = createTimePositionWithRelativeTimeAsOffset(bandStart.created)
			activeGenericBands.put(bandStart.type, genericBand)
		}

		void addEntity(BandEnd bandEnd) {
			GenericBand genericBand = activeGenericBands.remove(bandEnd.type)

			if (genericBand != null) {
				genericBand.bandEnd = bandEnd
				genericBand.endPosition = createTimePositionWithRelativeTimeAsOffset(bandEnd.created)
				timeline.addGenericBand(genericBand)
			} else {
				// expode???
			}
		}

		void addEntity(EditorActivity editorActivity) {
			TimePosition time = createTimePositionWithRelativeTimeAsOffset(editorActivity.created)
			ActivityDetail activityDetail = new ActivityDetail(time, editorActivity)
			timeline.addActivityDetail(activityDetail)
			relativeTime += editorActivity.duration
		}

		void addEntity(Conflict conflict) {
			//if already set, exploded!!
			activeConflictBand = new ConflictBand()
			activeConflictBand.conflict = conflict
			activeConflictBand.startPosition = createTimePositionWithRelativeTimeAsOffset(conflict.created)
		}

		private TimePosition createTimePositionWithRelativeTimeAsOffset(DateTime actualTime) {
			new TimePosition(actualTime, relativeTime)
		}

		void addEntity(Resolution resolution) {
			//if no active conflict, explode!!
			activeConflictBand.resolution = resolution
			activeConflictBand.endPosition = createTimePositionWithRelativeTimeAsOffset(resolution.created)
			timeline.addConflictBand(activeConflictBand)
			activeConflictBand = null
		}

		void addEntity(Note note) {
			TimePosition timePosition = createTimePositionWithRelativeTimeAsOffset(note.created)
			timeline.addEvent(new Event(timePosition, note))
		}

		void addEntity(Idle idle) {
			if (idle.comment) {
				Note note = new Note()
				note.id = idle.id
				note.comment = "[Idle] "+ idle.comment + "  ( " + Math.round(idle.duration / 60) + " minutes )"
				note.created = idle.created
				addEntity(note)
			}
		}

		void addEntity(StateChange stateChange) {
		}

		void addEntity(ModelEntity unknown) {
			throw new RuntimeException("Unknown ModelEntity type ${unknown?.class}")
		}

	}


}
