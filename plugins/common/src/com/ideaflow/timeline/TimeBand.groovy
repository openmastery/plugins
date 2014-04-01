package com.ideaflow.timeline

interface TimeBand {

	TimePosition getStartPosition()

	TimePosition getEndPosition()

	TimeDuration getDuration()

}
