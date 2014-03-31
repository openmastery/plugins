package com.ideaflow.timeline

import com.ideaflow.model.EditorActivity


class ActivityDetail extends RelativeTime {
	EditorActivity editorActivity

	ActivityDetail(int offset, EditorActivity editorActivity) {
		super(offset)
		this.editorActivity = editorActivity
	}
}
