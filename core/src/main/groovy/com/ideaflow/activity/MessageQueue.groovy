package com.ideaflow.activity

import com.ideaflow.controller.IFMController
import org.joda.time.LocalDateTime
import org.joda.time.Period
import org.openmastery.publisher.api.activity.NewEditorActivity
import org.openmastery.publisher.api.activity.NewExecutionActivity
import org.openmastery.publisher.api.activity.NewExternalActivity
import org.openmastery.publisher.api.activity.NewIdleActivity
import org.openmastery.publisher.api.activity.NewModificationActivity
import org.openmastery.publisher.api.batch.NewBatchEvent
import org.openmastery.publisher.api.event.EventType

class MessageQueue {

	private IFMController controller
	private MessageLogger messageLogger


	MessageQueue(IFMController controller, BatchPublisher batchPublisher, File queueDir, File allHistoryFile) {
		this.controller = controller
		this.messageLogger = new FileMessageLogger(batchPublisher, queueDir, allHistoryFile)
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

		NewBatchEvent batchEvent = NewBatchEvent.builder()
				.taskId(taskId)
				.endTime(LocalDateTime.now())
				.type(eventType)
				.comment(message)
				.build();

		messageLogger.writeMessage(batchEvent)
	}


	private boolean isDisabled() {
		controller.isRecording() == false
	}


	static class FileMessageLogger implements MessageLogger {
		private BatchPublisher batchPublisher
		private File queueDir
		private File activeMessageFile
		private File historyFile

		private final Object lock = new Object()
		private JSONConverter jsonConverter = new JSONConverter()

		private LocalDateTime lastBatchTime
		private int messageCount

		private final int BATCH_TIME_LIMIT_IN_SECONDS = 5
		private final int BATCH_MESSAGE_LIMIT = 500
		private static final String MESSAGE_FILE = "active_messages.log"

		FileMessageLogger(BatchPublisher batchPublisher, File queueDir, File historyFile) {
			this.batchPublisher = batchPublisher
			this.queueDir = queueDir
			activeMessageFile = new File(queueDir, MESSAGE_FILE)
			this.historyFile = historyFile

			lastBatchTime = LocalDateTime.now()
		}

		void writeMessage(Object message) {
			synchronized (lock) {
				if (isBatchThresholdReached()) {
					startNewBatch()
				}
				activeMessageFile.append(jsonConverter.toJSON(message) + "\n")
				historyFile.append(jsonConverter.toJSON(message) + "\n")
				messageCount++
			}
		}

		private boolean isBatchThresholdReached() {
			Period period = new Period(lastBatchTime, LocalDateTime.now())
			return messageCount > 0 && ((period.toStandardSeconds().seconds > BATCH_TIME_LIMIT_IN_SECONDS) ||
					messageCount > BATCH_MESSAGE_LIMIT)
		}

		private void startNewBatch() {
			synchronized (lock) {
				batchPublisher.commitBatch(activeMessageFile)
				activeMessageFile = new File(queueDir, MESSAGE_FILE)

				lastBatchTime = LocalDateTime.now()
				messageCount = 0
			}
		}

	}
}
