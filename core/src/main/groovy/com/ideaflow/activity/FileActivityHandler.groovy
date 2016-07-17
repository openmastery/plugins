package com.ideaflow.activity

import com.ideaflow.controller.IFMController
import org.joda.time.DateTime
import org.joda.time.Duration
import org.openmastery.publisher.client.ActivityClient

class FileActivityHandler {

	private static final int SHORTEST_ACTIVITY = 3

	private IFMController controller
	private ActivityClient activityClient
	private FileActivity activeFileActivity = null

	FileActivityHandler(IFMController controller, ActivityClient activityClient) {
		this.controller = controller
		this.activityClient = activityClient
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

		// TODO: append file activity to idle activity
		println "markIdleTime, idle=${idleDuration}, lastFileActivityDuration=${activeFileActivity?.getDurationInSeconds(idleDuration)}"
		activityClient.addIdleActivity(activeTaskId, idleDuration.standardSeconds)
	}

	void markExternalActivity(Duration idleDuration) {
		Long activeTaskId = activeTaskId
		if (activeTaskId == null) {
			return
		}

		println "activeFileActivity = ${activeFileActivity}"
		if (idleDuration.standardSeconds >= SHORTEST_ACTIVITY) {
			if (activeFileActivity != null) {
				println "markExternalActivity, duration=${idleDuration}, lastFileActivityDuration=${activeFileActivity?.getDurationInSeconds(idleDuration)}s"
				activityClient.addExternalActivity(activeTaskId, idleDuration.standardSeconds, null)
				activeFileActivity = createFileActivity(activeFileActivity.filePath)
			} else {
				println "markExternalActivity, duration=${idleDuration}"
				activityClient.addExternalActivity(activeTaskId, idleDuration.standardSeconds, null)
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
				println "saveFileActivity ${activeFileActivity}"
				activityClient.addEditorActivity(activeTaskId, activeFileActivity.durationInSeconds,
				                                 activeFileActivity.filePath, activeFileActivity.modified)
			}

			activeFileActivity = createFileActivity(filePath)
		}
	}

	void endFileEvent(String filePath) {
		if (isSame(filePath)) {
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

		public long getDurationInSeconds(Duration idleDuration) {
			Math.max(durationInSeconds - idleDuration.standardSeconds, 0)
		}

		public String toString() {
			"FileActivity [path=${filePath}, modified=${modified}, duration=${durationInSeconds}]"
		}
	}

}
