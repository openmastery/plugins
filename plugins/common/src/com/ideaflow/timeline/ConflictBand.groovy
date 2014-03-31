package com.ideaflow.timeline

import com.ideaflow.model.Conflict
import com.ideaflow.model.Resolution


class ConflictBand extends RelativeTime {
	Conflict conflict
	Resolution resolution

	ConflictBand(int offset, Conflict conflict, Resolution resolution) {
		super(offset)
		this.conflict = conflict
		this.resolution = resolution
	}
}
