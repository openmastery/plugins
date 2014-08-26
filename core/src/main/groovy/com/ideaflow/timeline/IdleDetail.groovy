package com.ideaflow.timeline

import com.ideaflow.model.Idle

class IdleDetail implements TimeEntry, Entity {

	private Idle idle
	final TimePosition time
	final TimeDuration duration

	IdleDetail(TimePosition timePosition, Idle idle) {
		this.idle = idle
		this.time = timePosition
		this.duration = new TimeDuration(idle.duration)
	}

	String getComment() {
		idle.comment
	}

	String getId() {
		idle.id
	}
}
