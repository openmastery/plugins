package com.ideaflow.activity

import com.ideaflow.controller.IFMController
import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.activity.NewEditorActivity
import org.openmastery.publisher.api.activity.NewExecutionActivity
import org.openmastery.publisher.api.activity.NewExternalActivity
import org.openmastery.publisher.api.activity.NewIdleActivity
import org.openmastery.publisher.api.activity.NewModificationActivity
import org.openmastery.publisher.api.event.EventType

class ActivityLogger {

	private IFMController controller
	private IFMLogger logger

	ActivityLogger(IFMController controller) {
		this.controller = controller
		this.logger = new IFMLogger()
	}

	private boolean isDisabled() {
		controller.isRecording() == false
	}

	void pushEditorActivity(Long taskId, Long durationInSeconds, String filePath, boolean isModified) {
		if (isDisabled()) {
			return
		}

		NewEditorActivity activity = NewEditorActivity.builder()
				.taskId(taskId)
				.endTime(LocalDateTime.now())
				.durationInSeconds(durationInSeconds)
				.filePath(filePath)
				.isModified(isModified)
				.build();

		logger.logEvent(activity.toString())
	}

	void pushModificationActivity(Long taskId, Long durationInSeconds, int modificationCount) {
		if (isDisabled()) {
			return
		}

		NewModificationActivity activity = NewModificationActivity.builder()
				.taskId(taskId)
				.endTime(LocalDateTime.now())
				.durationInSeconds(durationInSeconds)
				.modificationCount(modificationCount)
				.build();

		logger.logEvent(activity.toString())
	}

	void pushExecutionActivity(Long taskId, Long durationInSeconds, String processName,
	                           int exitCode,
	                           String executionTaskType,
	                           boolean isDebug) {
		if (isDisabled()) {
			return
		}

		NewExecutionActivity activity = NewExecutionActivity.builder()
				.taskId(taskId)
				.durationInSeconds(durationInSeconds)
				.endTime(LocalDateTime.now())
				.processName(processName)
				.exitCode(exitCode)
				.executionTaskType(executionTaskType)
				.isDebug(isDebug)
				.build();

		logger.logEvent(activity.toString())
	}

	void pushIdleActivity(Long taskId, Long durationInSeconds) {
		if (isDisabled()) {
			return
		}

		NewIdleActivity activity = NewIdleActivity.builder()
				.taskId(taskId)
				.endTime(LocalDateTime.now())
				.durationInSeconds(durationInSeconds)
				.build();

		logger.logEvent(activity.toString())
	}

	void pushExternalActivity(Long taskId, Long durationInSeconds, String comment) {
		if (isDisabled()) {
			return
		}

		NewExternalActivity activity = NewExternalActivity.builder()
				.taskId(taskId)
				.endTime(LocalDateTime.now())
				.durationInSeconds(durationInSeconds)
				.comment(comment)
				.build();

		logger.logEvent(activity.toString())
	}

	void pushEvent(Long taskId, EventType eventType, String message) {
		if (isDisabled()) {
			return
		}

		Event event = new Event(taskId, eventType, message)

		logger.logEvent(event.toString())
	}


	private static class Event {
		Long taskId
		EventType type
		String message

		Event(Long taskId, EventType type, String message) {
			this.taskId = taskId
			this.type = type
			this.message = message
		}

		String toString() {
			"Event(taskId=$taskId, type=${type.name()}, message=$message)"
		}
	}
}
