package com.ideaflow.timeline

import com.ideaflow.model.BandEnd
import com.ideaflow.model.BandStart
import com.ideaflow.model.BandType
import org.joda.time.DateTime

class GenericBand extends AbstractTimeBand {

	BandStart bandStart
	BandEnd bandEnd

	protected void setActivityStartCreated(DateTime created) {
		bandStart.created = created
	}

	protected void setActivityEndCreated(DateTime created) {
		bandEnd.created = created
	}

	BandType getBandType() {
		bandStart.type
	}

	String getId() {
		bandStart.getId()
	}

	String getComment() {
		bandStart.comment
	}

	String toString() {
		"GenericBand: $bandType"
	}
}
