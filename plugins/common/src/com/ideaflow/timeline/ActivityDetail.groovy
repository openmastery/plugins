package com.ideaflow.timeline

import com.ideaflow.model.EditorActivity
import org.joda.time.DateTime


class ActivityDetail extends RelativeTime {

	EditorActivity editorActivity

	ActivityDetail(int offset) {
		super(offset)
	}

	int getDuration() {
		editorActivity.duration
	}

	DateTime getCreated() {
		editorActivity.created
	}

}
