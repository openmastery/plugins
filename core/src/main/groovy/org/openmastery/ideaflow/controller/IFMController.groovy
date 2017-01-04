package org.openmastery.ideaflow.controller

import com.bancvue.rest.exception.NotFoundException
import org.openmastery.ideaflow.activity.BatchPublisher
import org.openmastery.ideaflow.activity.MessageQueue
import org.openmastery.ideaflow.state.TaskState
import org.joda.time.Duration
import org.openmastery.ideaflow.Logger
import org.openmastery.ideaflow.activity.ActivityHandler
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.client.BatchClient
import org.openmastery.publisher.client.TaskClient

class IFMController {

	private boolean paused = true
	private Logger logger
	private TaskClient taskClient
	private BatchClient batchClient
	private TaskState activeTask
	private ActivityHandler activityHandler
	private MessageQueue messageQueue
	private BatchPublisher batchPublisher


	IFMController(Logger logger) {
		this.logger = logger

		File ideaFlowDir = createIdeaFlowDir()

		batchPublisher = new BatchPublisher(ideaFlowDir, logger)
		messageQueue = new MessageQueue(this, batchPublisher)

		activityHandler = new ActivityHandler(this, messageQueue)

		new Thread(batchPublisher).start()
		startPushModificationActivityTimer(30)
	}

	private File createIdeaFlowDir() {
		File ideaFlowDir = new File(System.getProperty("user.home") + File.separator + ".ideaflow");
		ideaFlowDir.mkdirs()
		return ideaFlowDir
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
		// TODO: make these configurable
		taskClient = new TaskClient(apiUrl)
				.apiKey(apiKey)
				.connectTimeout(1000)
				.readTimeout(2000)
		batchClient = new BatchClient(apiUrl)
				.apiKey(apiKey)
				.connectTimeout(5000)
				.readTimeout(30000)
		batchPublisher.setBatchClient(batchClient)
	}

	Duration getRecentIdleDuration() {
		getActivityHandler().getRecentIdleDuration()
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
		TaskState task = TaskState.create(remoteTask)

		activateTask(task)
		return task
	}

	TaskState resumeAndActivateTask(String name) throws NoSuchTaskToResumeException {
		Task remoteTask

		try {
			remoteTask = taskClient.findTaskWithName(name)
		} catch (NotFoundException ex) {
			throw new NoSuchTaskToResumeException(name)
		}

		TaskState task = TaskState.create(remoteTask)

		activateTask(task)
		return task
	}

	void activateTask(TaskState newActiveTask) {
		setPaused(false)
		if (this.activeTask != null) {
			messageQueue.pushEvent(activeTask.id, EventType.DEACTIVATE, "Task-Switch: [" + activeTask?.name + "] to [" + newActiveTask.name + "]")
			messageQueue.pushEvent(newActiveTask.id, EventType.ACTIVATE, "Task-Switch: [" + activeTask?.name + "] to [" + newActiveTask.name + "]")
		} else {
			messageQueue.pushEvent(newActiveTask.id, EventType.ACTIVATE, "Task-Start: [" + newActiveTask.name + "]")
		}
		setActiveTask(newActiveTask)
	}

	void shutdown() {
		if (this.activeTask != null) {
			messageQueue.pushEvent(activeTask.id, EventType.DEACTIVATE, "IDE Shutdown")
		}
	}

	TaskState clearActiveTask() {
		TaskState oldActiveTask = activeTask
		activeTask = null
		oldActiveTask
	}

	boolean hasActiveTask() {
		activeTask != null
	}

	TaskState getActiveTask() {
		activeTask
	}

	Long getActiveTaskId() {
		activeTask?.id
	}

	String getActiveTaskName() {
		activeTask?.name
	}

	void createPain(String painMessage) {
		addUnresolvedPain(painMessage)
		createEvent(painMessage, EventType.WTF)
	}

	private void addUnresolvedPain(String painMessage) {
		if (activeTask.unresolvedPainList.size() > 10) {
			activeTask.unresolvedPainList.remove(0)
		}
		activeTask.unresolvedPainList.add(painMessage)
	}

	void resolveWithYay(String yayMessage) {
		activeTask.unresolvedPainList = []
		createEvent(yayMessage, EventType.AWESOME)
	}

	void createPainSnippet(String painMessage, String source, String snippet) {
		addUnresolvedPain(painMessage);
		messageQueue.pushSnippet(activeTask.id, EventType.WTF, painMessage, source, snippet)
	}

	void resolveWithAwesomeSnippet(String awesomeMessage, String source, String snippet) {
		activeTask.unresolvedPainList = []
		messageQueue.pushSnippet(activeTask.id, EventType.AWESOME, awesomeMessage, source, snippet)
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
