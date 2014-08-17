package com.newiron.ideaflow.presentation

import com.ideaflow.timeline.ActivityDetail
import com.ideaflow.timeline.Event
import com.ideaflow.timeline.TimeBand
import com.ideaflow.timeline.TimePosition
import com.ideaflow.timeline.Timeline
import groovy.json.JsonBuilder
import org.joda.time.DateTime


class TimelineChart {
	private Timeline timeline

	TimelineChart(Timeline timeline) {
		this.timeline = timeline
	}

	TimePosition getStart() {
		timeline.activityDetails.first().time
	}

	TimePosition getEnd() {
		ActivityDetail lastActivity = timeline.activityDetails.last()
		int duration = lastActivity.duration.duration
		DateTime endTime = lastActivity.time.actualTime.plusSeconds(duration)
		int endOffset = lastActivity.time.relativeOffset + duration
		return new TimePosition(endTime, endOffset)
	}

	List<Event> getEvents() {
		timeline.getEvents()
	}

	List<TimeBand> getTimeBands() {
		timeline.getTimeBands()
	}

	String toJSON() {
		def timelineData = [
			start: [offset: start.relativeOffset, shortTime: start.shortTime],
			end: [offset: end.relativeOffset, shortTime: end.shortTime],
			events: events.collect { Event event ->
				[offset: event.time.relativeOffset,
				 shortTime: event.time.shortTime,
				id: event.id]
			},
			timeBands: timeBands.collect { TimeBand timeBand ->
				[bandType: timeBand.bandType,
				 duration: timeBand.duration.duration,
				 offset: timeBand.startPosition.relativeOffset,
				id: timeBand.id]
			}
		]

		new JsonBuilder(timelineData).toString()
	}
}
