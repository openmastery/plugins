package com.ideaflow.controller

import com.ideaflow.activity.ActivityHandler
import com.ideaflow.activity.ActivityPublisher
import org.openmastery.publisher.api.ideaflow.IdeaFlowPartialCompositeState
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.client.ActivityClient
import org.openmastery.publisher.client.EventClient
import org.openmastery.publisher.client.IdeaFlowClient
import org.openmastery.publisher.client.TaskClient

class IFMController {

	private boolean enabled = false
	private IdeaFlowClient ideaFlowClient
	private EventClient eventClient
	private TaskClient taskClient
	private ActivityClient activityClient
	private Task activeTask
	private IdeaFlowPartialCompositeState activeTaskState
	private ActivityHandler activityHandler

	IFMController() {
		activityHandler = new ActivityHandler(this)
	}

	ActivityHandler getActivityHandler() {
		activityHandler
	}

	ActivityPublisher getActivityPublisher() {
		activityHandler.activityPublisher
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

	boolean isTaskActive() {
		enabled && (activeTask != null)
	}

	void setActiveTask(Task activeTask) {
		if (enabled) {
			this.activeTask = activeTask
			this.activeTaskState = activeTask != null ? ideaFlowClient.getActiveState(activeTask.id) : null
		}
	}

	Task getActiveTask() {
		activeTask
	}

	IdeaFlowPartialCompositeState getActiveTaskState() {
		activeTaskState
	}

	void newIdeaFlow(String name, String description) {
		if (enabled) {
			// TODO: what to do on conflict?
			Task newTask = taskClient.createTask(name, description);
			setActiveTask(newTask)
		}
	}

	String getActiveTaskName() {
		activeTask?.name
	}

	// TODO: this method shoudl go away - we should be returning the active state on each band transition
	private void setActiveTaskState() {
		activeTaskState = activeTask != null ? ideaFlowClient.getActiveState(activeTask.id) : null
	}

	void startConflict(String question) {
		if (activeTask) {
			ideaFlowClient.startConflict(activeTask.id, question)
			setActiveTaskState()
		}
	}

	void endConflict(String answer) {
		if (activeTask) {
			ideaFlowClient.endConflict(activeTask.id, answer)
			setActiveTaskState()
		}
	}

	void startBand(String comment, IdeaFlowStateType type) {
		if (activeTask) {
			ideaFlowClient.startBand(activeTask.id, comment, type)
			setActiveTaskState()
		}
	}

	void endBand(String comment, IdeaFlowStateType type) {
		if (activeTask) {
			ideaFlowClient.endBand(activeTask.id, comment, type)
			setActiveTaskState()
		}
	}

	void addNote(String message) {
		if (activeTask && message) {
			eventClient.addUserNote(activeTask.id, message)
			setActiveTaskState()
		}
	}

	List<Task> getRecentTasks() {
		enabled ? taskClient.findRecentTasks(5) : []
	}

}
