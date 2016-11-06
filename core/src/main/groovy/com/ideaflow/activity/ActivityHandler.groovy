package com.ideaflow.activity

import com.ideaflow.controller.IFMController
import org.joda.time.Duration
import org.joda.time.LocalDateTime
import org.joda.time.Period
import org.openmastery.publisher.api.event.EventType

import java.util.concurrent.atomic.AtomicInteger

class ActivityHandler {

	private static final int SHORTEST_ACTIVITY = 3

	private IFMController controller
	private ActivityLogger activityLogger
	private FileActivity activeFileActivity
	private AtomicInteger modificationCount = new AtomicInteger(0)


	private Map<Long, ProcessActivity> activeProcessMap =[:]

	ActivityHandler(IFMController controller) {
		this.controller = controller
		this.activityLogger = new ActivityLogger(controller)
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

		activityLogger.pushIdleActivity(activeTaskId, idleDuration.standardSeconds)
	}

	void markExternalActivity(Duration idleDuration) {
		Long activeTaskId = activeTaskId
		if (activeTaskId == null) {
			return
		}

		if (idleDuration.standardSeconds >= SHORTEST_ACTIVITY) {
			activityLogger.pushExternalActivity(activeTaskId, idleDuration.standardSeconds, null)
			if (activeFileActivity != null) {
				activeFileActivity = createFileActivity(activeFileActivity.filePath)
			}
		}
	}

	void markProcessStarting(Long processId, String processName, String executionTaskType, boolean isDebug) {
		ProcessActivity processActivity = new ProcessActivity(processName: processName, executionTaskType: executionTaskType, isDebug: isDebug, timeStarted: LocalDateTime.now())
		activeProcessMap.put(processId, processActivity)
		//TODO this will leak memory if the processes started are never closed
	}

	void markProcessEnding(Long processId, int exitCode) {
		ProcessActivity processActivity = activeProcessMap.remove(processId)
		Long activeTaskId = activeTaskId
		if (processActivity && activeTaskId != null) {
			activityLogger.pushExecutionActivity(activeTaskId, processActivity.durationInSeconds, processActivity.processName,
					exitCode, processActivity.executionTaskType, processActivity.isDebug)
		}
	}

	void startFileEvent(String filePath) {
		Long activeTaskId = activeTaskId
		if (activeTaskId == null) {
			return
		}

		if (isDifferent(filePath)) {
			if (isOverActivityThreshold()) {
				activityLogger.pushEditorActivity(activeTaskId, activeFileActivity.durationInSeconds,
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
		modificationCount.incrementAndGet()
	}

	void pushModificationActivity(Long intervalInSeconds) {
		int modificationCount = modificationCount.getAndSet(0)
		if (modificationCount > 0) {
			activityLogger.pushModificationActivity(activeTaskId, intervalInSeconds, modificationCount)
		}
	}

	void createEvent(String message, EventType eventType) {
		Long activeTaskId = activeTaskId
		if (activeTaskId == null) {
			return
		}
		activityLogger.pushEvent(activeTaskId, eventType, message)
	}

	private FileActivity createFileActivity(filePath) {
		filePath == null ? null : new FileActivity(filePath: filePath, time: LocalDateTime.now(), modified: false)
	}

	private static class ProcessActivity {
		LocalDateTime timeStarted
		String processName
		String executionTaskType
		boolean isDebug

		public long getDurationInSeconds() {
			Period.fieldDifference(timeStarted, LocalDateTime.now()).millis / 1000
		}

		public String toString() {
			"ProcessActivity [processName=${processName}, executionTaskType=${executionTaskType}, duration=${durationInSeconds}, isDebug=${isDebug}]"
		}
	}

	private static class FileActivity {
		LocalDateTime time
		String filePath
		boolean modified

		public long getDurationInSeconds() {

			Period.fieldDifference(time, LocalDateTime.now()).millis / 1000
		}

		public String toString() {
			"FileActivity [path=${filePath}, modified=${modified}, duration=${durationInSeconds}]"
		}
	}

}
