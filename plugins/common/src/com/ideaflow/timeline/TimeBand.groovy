package com.ideaflow.timeline

import com.ideaflow.model.BandEnd
import com.ideaflow.model.BandStart


class TimeBand extends RelativeTime {

	BandStart bandStart
	BandEnd bandEnd

	TimeBand(int offset, BandStart bandStart, BandEnd bandEnd) {
		super(offset)
		this.bandStart = bandStart
		this.bandEnd = bandEnd
	}

}
