package com.ideaflow.timeline

interface TimeBand {

	TimePosition getStartPosition()

	TimePosition getEndPosition()

	TimeDuration getDuration()

	void setStartPosition(TimePosition startPosition)

	void setEndPosition(TimePosition endPosition)
}
