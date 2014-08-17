package com.ideaflow.timeline

import com.ideaflow.model.EditorActivity

class ActivityDetail implements TimeEntry, Entity {

	private EditorActivity editorActivity
	final TimePosition time
	final TimeDuration duration

	ActivityDetail(TimePosition timePosition, EditorActivity editorActivity) {
		this.editorActivity = editorActivity
		this.time = timePosition
		this.duration = new TimeDuration(editorActivity.duration)
	}

	String getActivityName() {
		editorActivity.name
	}

	
	boolean isModified() {
		editorActivity.modified
	}

	String getId() {
		editorActivity.id
	}
}
