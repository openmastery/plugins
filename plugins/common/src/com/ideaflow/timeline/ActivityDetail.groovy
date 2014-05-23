package com.ideaflow.timeline

import com.ideaflow.model.EditorActivity
import org.joda.time.DateTime


class ActivityDetail {

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


}
