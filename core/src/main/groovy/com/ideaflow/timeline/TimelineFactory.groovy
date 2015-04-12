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
		new TimelineTimeBandContainerBuilder().addTimeBandContainersToTimeline(builder.timeline)
		builder.timeline
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
				// TODO: expode???
			}
		}

		void addEntity(EditorActivity editorActivity) {
			TimePosition time = createTimePositionWithRelativeTimeAsOffset(editorActivity.created)
			ActivityDetail activityDetail = new ActivityDetail(time, editorActivity)
			timeline.addActivityDetail(activityDetail)
			relativeTime += editorActivity.duration
		}

		void addEntity(Conflict conflict) {
			// TODO: if already set, exploded!!
			activeConflictBand = new ConflictBand()
			activeConflictBand.conflict = conflict
			activeConflictBand.startPosition = createTimePositionWithRelativeTimeAsOffset(conflict.created)
		}

		private TimePosition createTimePositionWithRelativeTimeAsOffset(DateTime actualTime) {
			new TimePosition(actualTime, relativeTime)
		}

		void addEntity(Resolution resolution) {
			// TODO: if no active conflict, explode!!
			activeConflictBand.resolution = resolution
			activeConflictBand.endPosition = createTimePositionWithRelativeTimeAsOffset(resolution.created)
			timeline.addConflictBand(activeConflictBand)
		}

		void addEntity(Note note) {
			TimePosition timePosition = createTimePositionWithRelativeTimeAsOffset(note.created)
			timeline.addEvent(new Event(timePosition, note))
		}

		void addEntity(Idle idle) {
			TimePosition timePosition = createTimePositionWithRelativeTimeAsOffset(idle.created)
			timeline.addIdleDetail(new IdleDetail(timePosition, idle) )
		}

		void addEntity(StateChange stateChange) {
		}

		void addEntity(ModelEntity unknown) {
			throw new RuntimeException("Unknown ModelEntity type ${unknown?.class}")
		}

	}

	private static class TimelineTimeBandContainerBuilder {
		void addTimeBandContainersToTimeline(Timeline timeline) {
			TimeBandContainer activeContainer
			ConflictBand lastConflictBand

			List<TimeBand> sortedTimeBands = getTimeBandsSortedByStartTime(timeline)
			sortedTimeBands.each { AbstractTimeBand timeBand ->
				if (activeContainer == null) {
					if (timeBand instanceof GenericBand) {
						activeContainer = createTimeBandContainer(timeBand, lastConflictBand)
					}
				} else {
					if (timeBand instanceof GenericBand) {
						throw new RuntimeException("GenericBand nesting not supported, band=${timeBand} " +
								"started before end of ${activeContainer.primaryGenericBand}")
					} else if (timeBand.endsBefore(activeContainer.primaryGenericBand)) {
						activeContainer.addTimeBand(timeBand)
					} else {
						if (!activeContainer.isEmpty()) {
							timeline.addTimeBandContainer(activeContainer)
						}
						activeContainer = null
					}
				}

				if (timeBand instanceof ConflictBand) {
					lastConflictBand = (ConflictBand) timeBand
				}
			}

			if (activeContainer && !activeContainer.isEmpty()) {
				timeline.addTimeBandContainer(activeContainer)
			}
		}

		private TimeBandContainer createTimeBandContainer(GenericBand genericBand, ConflictBand lastConflictBand) {
			ConflictBand linkedConflictBand = genericBand.isLinkedToParentConflict() ? lastConflictBand : null
			new TimeBandContainer(genericBand, linkedConflictBand)
		}

		private List<TimeBand> getTimeBandsSortedByStartTime(Timeline timeline) {
			(timeline.conflictBands + timeline.genericBands).sort {
				TimeBand timeBand ->
					timeBand.startPosition.relativeOffset
			}
		}

	}

}
