package com.ideaflow.timeline

import com.ideaflow.model.Note


class Event extends RelativeTime {
	Note note

	Event(int offset, Note note) {
		super(offset)
		this.note = note
	}
}
