package com.ideaflow.activity

import com.ideaflow.controller.IFMController
import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.activity.NewEditorActivity
import org.openmastery.publisher.api.activity.NewExecutionActivity
import org.openmastery.publisher.api.activity.NewExternalActivity
import org.openmastery.publisher.api.activity.NewIdleActivity
import org.openmastery.publisher.api.activity.NewModificationActivity
import org.openmastery.publisher.api.event.EventType

class MessageQueue {

	private IFMController controller
	private MessageLogger messageLogger

	private static final String MESSAGE_FILE = "active_messages.log"
	private static final String BATCH_FILE_PREFIX = "batch_"


	MessageQueue(IFMController controller) {
		this.controller = controller
		this.messageLogger = new FileMessageLogger()
	}

	MessageQueue(IFMController controller, MessageLogger messageLogger) {
		this.controller = controller
		this.messageLogger = messageLogger
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

		messageLogger.writeMessage(activity)
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

		messageLogger.writeMessage(activity)
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

		messageLogger.writeMessage(activity)
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

		messageLogger.writeMessage(activity)
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

		messageLogger.writeMessage(activity)
	}

	void pushEvent(Long taskId, EventType eventType, String message) {
		if (isDisabled()) {
			return
		}

		Event event = new Event(taskId, eventType, message)

		messageLogger.writeMessage(event)
	}


	private boolean isDisabled() {
		controller.isRecording() == false
	}


	static class FileMessageLogger implements MessageLogger {
		private File activeLog
		private File logDir
		private final Object lock = new Object()

		FileMessageLogger() {
			logDir = new File(System.getProperty("user.home") + File.separator + ".ideaflow");
			logDir.mkdirs()

			activeLog = new File(logDir, MESSAGE_FILE)
		}

		void writeMessage(Object message) {
			synchronized (lock) {
				activeLog.append("\n${message.toString()}")
			}
		}

		private void startNewBatch() {
			synchronized (lock) {
				activeLog.renameTo(BATCH_FILE_PREFIX + createTimestampSuffix())
				activeLog = new File(logDir, MESSAGE_FILE)
			}

		}

		String createTimestampSuffix() {
			LocalDateTime now = LocalDateTime.now()

			now.toString("yyyy-MM-dd-HH-mm")
		}

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
