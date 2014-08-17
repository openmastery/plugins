package com.ideaflow.timeline

import com.ideaflow.model.Note


class Event implements TimeEntry, Entity {
	TimePosition time
	Note note

	Event(TimePosition timePosition, Note note) {
		this.time = timePosition
		this.note = note
	}

	void setTime(TimePosition timePosition) {
		note.created = timePosition.actualTime
		time = timePosition
	}

	String getComment() {
		note.comment
	}

	String getId() {
		note.id
	}
}
