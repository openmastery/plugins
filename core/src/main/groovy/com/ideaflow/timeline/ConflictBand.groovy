package com.ideaflow.timeline

import com.ideaflow.model.BandType
import com.ideaflow.model.Conflict
import com.ideaflow.model.Resolution
import org.joda.time.DateTime


class ConflictBand extends AbstractTimeBand implements Entity {

	private Conflict conflict
	private Resolution resolution

	protected void setActivityStartCreated(DateTime created) {
		conflict.created = created
	}

	protected void setActivityEndCreated(DateTime created) {
		resolution.created = created
	}

	String getId() {
		conflict.getId()
	}

	String getComment() {
		conflict.question
	}

	String getQuestion() {
		conflict.question
	}

	String getAnswer() {
		resolution.answer
	}

	String getMistakeType() {
		conflict.mistakeType
	}

	String getCause() {
		conflict.cause
	}

	String getNotes() {
		conflict.notes
	}

	BandType getBandType() {
		BandType.conflict
	}

	String toString() {
		"ConflictBand: $question"
	}

}
