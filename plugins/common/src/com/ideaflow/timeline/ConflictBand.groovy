package com.ideaflow.timeline

import com.ideaflow.model.Conflict
import com.ideaflow.model.Resolution


class ConflictBand extends RelativeTime {

	int duration
	Conflict conflict
	Resolution resolution

	ConflictBand(int offset) {
		super(offset)
	}

}
