package com.ideaflow.timeline

import org.joda.time.DateTime

abstract class AbstractTimeBand implements TimeBand, TimeEntry {

	TimePosition startPosition
	TimePosition endPosition
	TimeDuration duration

	protected abstract void setActivityStartCreated(DateTime created)

	protected abstract void setActivityEndCreated(DateTime created)

	abstract String getId()

	void setStartPosition(TimePosition startPosition) {
		this.startPosition = startPosition
		setActivityStartCreated(startPosition.actualTime)
		initDuration()
	}

	void setEndPosition(TimePosition endPosition) {
		this.endPosition = endPosition
		setActivityEndCreated(endPosition.actualTime)
		initDuration()
	}

	TimePosition getTime() {
		return startPosition
	}


	private void initDuration() {
		if (startPosition && endPosition) {
			duration = new TimeDuration(endPosition.relativeOffset - startPosition.relativeOffset)
		}
	}

}
