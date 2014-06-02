package com.ideaflow.timeline

import com.ideaflow.model.BandType

interface TimeBand {

	TimePosition getStartPosition()

	TimePosition getEndPosition()

	TimeDuration getDuration()

	void setStartPosition(TimePosition startPosition)

	void setEndPosition(TimePosition endPosition)

	BandType getBandType()

	String getId()
}
