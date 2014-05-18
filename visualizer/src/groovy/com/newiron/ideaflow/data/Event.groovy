package com.newiron.ideaflow.data

import com.ideaflow.model.Note


class Event extends com.ideaflow.timeline.Event {
    @Delegate TimePosition time

	Event(int hour, int min, int seconds, String comment) {
		super(0, new Note(comment))
		time = new TimePosition(hour, min, seconds)
		offset = time.relativeOffset
	}

	String getComment() {
		note.comment
	}

}
