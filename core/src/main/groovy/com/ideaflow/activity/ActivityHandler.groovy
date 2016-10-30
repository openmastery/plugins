package com.ideaflow.activity

import com.ideaflow.controller.IFMController
import org.joda.time.DateTime
import org.joda.time.Duration
import org.openmastery.publisher.client.ActivityClient

class ActivityHandler {

	private static final int SHORTEST_ACTIVITY = 3

	private IFMController controller
	private ActivityQueue activityQueue
	private FileActivity activeFileActivity = null
	private ActivityPublisher activityPublisher

	ActivityHandler(IFMController controller) {
		this.controller = controller
		this.activityQueue = new ActivityQueue()
		this.activityPublisher = new ActivityPublisher(activityQueue)
	}

	void setActivityClient(ActivityClient activityClient) {
		activityQueue.setActivityClient(activityClient)
	}

	ActivityPublisher getActivityPublisher() {
		activityPublisher
	}

	private boolean isSame(String newFilePath) {
		isDifferent(newFilePath) == false
	}

	private boolean isDifferent(String newFilePath) {
		if (activeFileActivity == null) {
			return newFilePath != null
		} else {
			return activeFileActivity.filePath != newFilePath
		}
	}

	private boolean isOverActivityThreshold() {
		activeFileActivity != null && activeFileActivity.durationInSeconds >= SHORTEST_ACTIVITY
	}

	private Long getActiveTaskId() {
		controller.getActiveTask()?.id
	}

	void markIdleTime(Duration idleDuration) {
		Long activeTaskId = activeTaskId
		if (activeTaskId == null) {
			return
		}

		activityQueue.pushIdleActivity(activeTaskId, idleDuration.standardSeconds)
	}

	void markExternalActivity(Duration idleDuration) {
		Long activeTaskId = activeTaskId
		if (activeTaskId == null) {
			return
		}

		if (idleDuration.standardSeconds >= SHORTEST_ACTIVITY) {
			if (activeFileActivity != null) {
				activityQueue.pushExternalActivity(activeTaskId, idleDuration.standardSeconds, null)
				activeFileActivity = createFileActivity(activeFileActivity.filePath)
			} else {
				activityQueue.pushExternalActivity(activeTaskId, idleDuration.standardSeconds, null)
			}
		}
	}

	void startFileEvent(String filePath) {
		Long activeTaskId = activeTaskId
		if (activeTaskId == null) {
			return
		}

		if (isDifferent(filePath)) {
			if (isOverActivityThreshold()) {
				activityQueue.pushEditorActivity(activeTaskId, activeFileActivity.durationInSeconds,
				                                 activeFileActivity.filePath, activeFileActivity.modified)
			}

			activeFileActivity = createFileActivity(filePath)
		}
	}

	void endFileEvent(String filePath) {
		if ((filePath == null) || isSame(filePath)) {
			startFileEvent(null)
		}
	}

	void fileModified(String filePath) {
		if (activeFileActivity?.filePath == filePath) {
			activeFileActivity.modified = true
		}
	}

	private FileActivity createFileActivity(filePath) {
		filePath == null ? null : new FileActivity(filePath: filePath, time: DateTime.now(), modified: false)
	}

	private static class FileActivity {
		DateTime time
		String filePath
		boolean modified

		public long getDurationInSeconds() {
			new Duration(time, DateTime.now()).standardSeconds
		}

		public String toString() {
			"FileActivity [path=${filePath}, modified=${modified}, duration=${durationInSeconds}]"
		}
	}

}
