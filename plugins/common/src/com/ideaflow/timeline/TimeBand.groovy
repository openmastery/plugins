package com.ideaflow.timeline

import com.ideaflow.model.BandEnd
import com.ideaflow.model.BandStart


class TimeBand extends RelativeTime {

	int duration
	BandStart bandStart
	BandEnd bandEnd

	TimeBand(int offset) {
		super(offset)
	}

}
