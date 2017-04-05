package org.openmastery.ideaflow.activity

import org.joda.time.LocalDateTime
import org.joda.time.Period
import org.openmastery.ideaflow.controller.IFMController
import org.openmastery.publisher.api.activity.NewEditorActivity
import org.openmastery.publisher.api.activity.NewExecutionActivity
import org.openmastery.publisher.api.activity.NewExternalActivity
import org.openmastery.publisher.api.activity.NewIdleActivity
import org.openmastery.publisher.api.activity.NewModificationActivity
import org.openmastery.publisher.api.batch.NewBatchEvent
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.event.NewSnippetEvent

class MessageQueue {

	private IFMController controller
	private MessageLogger messageLogger


	MessageQueue(IFMController controller, BatchPublisher batchPublisher) {
		this.controller = controller
		this.messageLogger = new FileMessageLogger(batchPublisher)
	}

	MessageQueue(IFMController controller, MessageLogger messageLogger) {
		this.controller = controller
		this.messageLogger = messageLogger
	}

	void flush() {
		messageLogger.flush()
	}

	void pushEditorActivity(Long taskId, Long durationInSeconds, String filePath, boolean isModified) {
		pushEditorActivity(taskId, durationInSeconds, LocalDateTime.now(), filePath, isModified)
	}

	void pushEditorActivity(Long taskId, Long durationInSeconds, LocalDateTime endTime, String filePath, boolean isModified) {
		if (isDisabled()) {
			return
		}

		NewEditorActivity activity = NewEditorActivity.builder()
				.taskId(taskId)
				.endTime(endTime)
				.durationInSeconds(durationInSeconds)
				.filePath(filePath)
				.isModified(isModified)
				.build();

		messageLogger.writeMessage(taskId, activity)
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

		messageLogger.writeMessage(taskId, activity)
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

		messageLogger.writeMessage(taskId, activity)
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

		messageLogger.writeMessage(taskId, activity)
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

		messageLogger.writeMessage(taskId, activity)
	}

	void pushEvent(Long taskId, EventType eventType, String message) {
		if (isDisabled()) {
			return
		}

		NewBatchEvent batchEvent = NewBatchEvent.builder()
				.taskId(taskId)
				.position(LocalDateTime.now())
				.type(eventType)
				.comment(message)
				.build();

		messageLogger.writeMessage(taskId, batchEvent)
	}

	void pushSnippet(Long taskId, EventType eventType, String message, String source, String snippet) {
		if (isDisabled()) {
			return
		}

		NewSnippetEvent batchEvent = NewSnippetEvent.builder()
				.taskId(taskId)
				.position(LocalDateTime.now())
				.eventType(eventType)
				.comment(message)
				.source(source)
				.snippet(snippet)
				.build()

		messageLogger.writeMessage(taskId, batchEvent)
	}


	private boolean isDisabled() {
		controller.isRecording() == false
	}


	static class FileMessageLogger implements MessageLogger {
		private BatchPublisher batchPublisher
		private Map<Long, File> activeMessageFiles = new HashMap<>()

		private final Object lock = new Object()
		private JSONConverter jsonConverter = new JSONConverter()

		private LocalDateTime lastBatchTime
		private int messageCount

		private final int BATCH_TIME_LIMIT_IN_SECONDS = 30 * 60
		private final int BATCH_MESSAGE_LIMIT = 500

		FileMessageLogger(BatchPublisher batchPublisher) {
			this.batchPublisher = batchPublisher

			lastBatchTime = LocalDateTime.now()
		}

		void flush() {
			startNewBatch()
		}

		void writeMessage(Long taskId, Object message) {
			String messageAsJson = jsonConverter.toJSON(message)

			synchronized (lock) {
				if (isBatchThresholdReached()) {
					startNewBatch()
				}
				getFileForTask(taskId).append(messageAsJson + "\n")
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
				batchPublisher.commitActiveFiles()
				activeMessageFiles.clear()

				lastBatchTime = LocalDateTime.now()
				messageCount = 0
			}
		}

		private File getFileForTask(Long taskId) {
			synchronized (lock) {
				File file = activeMessageFiles.get(taskId)
				if (file == null) {
					file = batchPublisher.createActiveFile("${taskId}.log")
					activeMessageFiles.put(taskId, file)
				}
				file
			}
		}

	}
}
