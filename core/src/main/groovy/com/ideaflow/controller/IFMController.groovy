package com.ideaflow.controller

import com.ideaflow.activity.ActivityHandler
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.client.ActivityClient
import org.openmastery.publisher.client.EventClient
import org.openmastery.publisher.client.IdeaFlowClient
import org.openmastery.publisher.client.TaskClient

class IFMController {

	private boolean enabled = false
	private boolean paused = true
	private IdeaFlowClient ideaFlowClient
	private EventClient eventClient
	private TaskClient taskClient
	private ActivityClient activityClient
	private Task activeTask
	private ActivityHandler activityHandler

	IFMController() {
		activityHandler = new ActivityHandler(this)

		new Thread(activityHandler.activityPublisher).start()
		startPushModificationActivityTimer(30)
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
		// TODO: need to validate apiKey

		ideaFlowClient = new IdeaFlowClient(apiUrl)
				.apiKey(apiKey)
		eventClient = new EventClient(apiUrl)
				.apiKey(apiKey)
		taskClient = new TaskClient(apiUrl)
				.apiKey(apiKey)
		activityClient = new ActivityClient(apiUrl)
				.apiKey(apiKey)
		activityHandler.setActivityClient(activityClient)
		enabled = true
	}

	boolean isEnabled() {
		enabled
	}

	boolean isPaused() {
		paused
	}

	void setPaused(boolean paused) {
		this.paused = paused
	}

	boolean isRecording() {
		enabled && (paused == false)
	}

	boolean isTaskActive() {
		enabled && (activeTask != null)
	}

	void setActiveTask(Task activeTask) {
		if (enabled) {
			this.activeTask = activeTask
		}
	}

	Task getActiveTask() {
		activeTask
	}

	Task newTask(String name, String description) {
		Task newTask = null
		if (enabled) {
			// TODO: what to do on conflict?
			newTask = taskClient.createTask(name, description);
			setActiveTask(newTask)
		}
		newTask
	}

	String getActiveTaskName() {
		activeTask?.name
	}

	void createSubtask(String message) {
		if (activeTask && message) {
			eventClient.createSubtask(activeTask.id, message)
		}
	}

	void createWTF(String message) {
		if (activeTask && message) {
			eventClient.createWTF(activeTask.id, message)
		}
	}

	void createAwesome(String message) {
		if (activeTask && message) {
			eventClient.createAwesome(activeTask.id, message)
		}
	}

}
