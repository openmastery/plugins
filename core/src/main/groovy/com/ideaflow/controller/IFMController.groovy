package com.ideaflow.controller

import com.ideaflow.activity.ActivityHandler
import com.ideaflow.activity.BatchPublisher
import com.ideaflow.activity.MessageQueue
import com.ideaflow.state.TaskState
import com.ideaflow.state.TimeConverter
import org.joda.time.LocalDateTime
import org.joda.time.Seconds
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.client.BatchClient
import org.openmastery.publisher.client.EventClient
import org.openmastery.publisher.client.IdeaFlowClient
import org.openmastery.publisher.client.TaskClient

class IFMController {

	private boolean paused = true
	private IdeaFlowClient ideaFlowClient
	private EventClient eventClient
	private TaskClient taskClient
	private BatchClient batchClient
	private TaskState activeTask
	private ActivityHandler activityHandler
	private MessageQueue messageQueue
	private BatchPublisher batchPublisher


	IFMController() {
		File messageQueueDir = createMessageQueueDir()

		batchPublisher = new BatchPublisher(messageQueueDir)
		messageQueue = new MessageQueue(this, batchPublisher, messageQueueDir)

		activityHandler = new ActivityHandler(this, messageQueue)

		new Thread(batchPublisher).start()
		startPushModificationActivityTimer(30)
	}

	private File createMessageQueueDir() {
		File queueDir = new File(System.getProperty("user.home") + File.separator + ".ideaflow");
		queueDir.mkdirs()
		return queueDir
	}

	private void startPushModificationActivityTimer(final long intervalInSeconds) {
		TimerTask timerTask = new TimerTask() {
			@Override
			void run() {
				activityHandler.pushModificationActivity(intervalInSeconds)
			}
		}

		long intervalInMillis = intervalInSeconds * 1000
		new Timer().scheduleAtFixedRate(timerTask, intervalInMillis, intervalInMillis)
	}

	ActivityHandler getActivityHandler() {
		activityHandler
	}

	void initClients(String apiUrl, String apiKey) {

		ideaFlowClient = new IdeaFlowClient(apiUrl)
				.apiKey(apiKey)
		eventClient = new EventClient(apiUrl)
				.apiKey(apiKey)
		taskClient = new TaskClient(apiUrl)
				.apiKey(apiKey)
		batchClient = new BatchClient(apiUrl)
				.apiKey(apiKey)
		batchPublisher.setBatchClient(batchClient)
	}

	boolean isEnabled() {
		true
	}

	boolean isPaused() {
		paused
	}

	void setPaused(boolean paused) {
		this.paused = paused
	}

	boolean isRecording() {
		(paused == false)
	}

	boolean isTaskActive() {
		(activeTask != null)
	}

	void setActiveTask(TaskState newActiveTask) {
			this.activeTask = newActiveTask
	}

	TaskState createAndActivateTask(String name, String description, String project) {
		// TODO: what to do on conflict?  Do we still need to activate tasks?

		Task remoteTask = taskClient.createTask(name, description, project);
		TaskState task = new TaskState(id: remoteTask.id, name: remoteTask.name, description: remoteTask.description)

		activateTask(task)
		return task
	}

	void activateTask(TaskState newActiveTask) {
		setPaused(false)
		if (this.activeTask != null) {
			messageQueue.pushEvent(activeTask.id,  EventType.DEACTIVATE, "Task-Switch: ["+activeTask?.name +"] to ["+newActiveTask.name+"]")
			messageQueue.pushEvent(newActiveTask.id, EventType.ACTIVATE, "Task-Switch: ["+activeTask?.name +"] to ["+newActiveTask.name+"]")
		} else {
			messageQueue.pushEvent(newActiveTask.id, EventType.ACTIVATE, "Task-Start: ["+newActiveTask.name+"]")
		}
		setActiveTask(newActiveTask)
	}

	void shutdown() {
		if (this.activeTask != null) {
			messageQueue.pushEvent(activeTask.id, EventType.DEACTIVATE, "IDE Shutdown")
		}
	}

	TaskState getActiveTask() {
		activeTask
	}

	String getActiveTaskName() {
		activeTask?.name
	}

	void createWTF(String wtfMessage) {
		if (activeTask.unresolvedWTFList == null) {
			activeTask.unresolvedWTFList = []
		}
		if (activeTask.unresolvedWTFList.size() > 10) {
			activeTask.unresolvedWTFList.remove(0)
		}
		activeTask.unresolvedWTFList.add(wtfMessage)
		createEvent(wtfMessage, EventType.WTF)
	}

	void resolveWithYay(String yayMessage) {
		activeTask.unresolvedWTFList = []
		createEvent(yayMessage, EventType.AWESOME)
	}

	void createEvent(String message, EventType eventType) {
		messageQueue.pushEvent(activeTask.id, eventType, message)
	}

	private static class InvalidApiKeyException extends RuntimeException {
		InvalidApiKeyException(String apiKey) {
			super("The server did not recognize the provided API Key '${apiKey}'")
		}
	}

	private static class FailedToConnectException extends RuntimeException {
		FailedToConnectException(String apiUrl) {
			super("Failed to connect to server, url=${apiUrl}")
		}
	}


}
