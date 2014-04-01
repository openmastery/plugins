package com.ideaflow.timeline

import com.ideaflow.model.Conflict
import com.ideaflow.model.Resolution
import org.joda.time.DateTime


class ConflictBand extends AbstractTimeBand {

	Conflict conflict
	Resolution resolution

	protected void setActivityStartCreated(DateTime created) {
		conflict.created = created
	}

	protected void setActivityEndCreated(DateTime created) {
		resolution.created = created
	}

}
