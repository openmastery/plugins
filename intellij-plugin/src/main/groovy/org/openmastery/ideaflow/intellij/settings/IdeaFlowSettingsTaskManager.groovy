package org.openmastery.ideaflow.intellij.settings

import groovy.json.JsonException
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent
import org.openmastery.ideaflow.intellij.Logger
import org.openmastery.ideaflow.state.TaskState

/**
 * TODO: JsonOutput and LocalDateTime apparently do not mix... throws stack overflow if a Task is serialized to json
 * find a more maintainable way to persist this data!!!
 */
public class IdeaFlowSettingsTaskManager {

	private IdeaFlowSettings settings;
	private Logger logger;

	public IdeaFlowSettingsTaskManager(IdeaFlowSettings settings) {
		this.settings = settings;
		this.logger = IdeaFlowApplicationComponent.log
	}

	List<TaskState> getRecentTasks() {
		List<Map> recentTasks = new JsonSlurper().parseText(settings.taskListJsonString ?: '[]')
		recentTasks.collect { createTask(it) }
	}

	private TaskState createTask(Map properties) {
		TaskState taskState = new TaskState()
		properties.each { propName, value ->
			if (taskState.hasProperty(propName)) {
				taskState."$propName" = value
			} else {
				logger.warn("Failed to apply property=${propName}, value=${value} - no property with that name on TaskState and will be ignored")
			}
		}
		return taskState

	}


	private List<TaskState> getTaskStateList() {
		List<Map> itemsAsMap
		try {
			itemsAsMap = new JsonSlurper().parseText(settings.taskListJsonString ?: '[]') as List<Map>
		} catch (JsonException ex) {
			logger.error("JSON error while attempting to convert task list=${settings.taskListJsonString}, clearing out task list")
			itemsAsMap = []
		}
		itemsAsMap.collect { Map properties ->
			properties as TaskState
		}
	}

	public void addRecentTask(TaskState task) {
		if (task != null) {
			List<TaskState> taskStateList = getTaskStateList()

			taskStateList.add(0, task)

			int maxListSize = settings.recentTaskListSize
			if (taskStateList.size() > maxListSize) {
				taskStateList = taskStateList.subList(0, maxListSize)
			}

			settings.taskListJsonString = JsonOutput.toJson(taskStateList)
		}
	}

	public void removeTask(TaskState task) {
		List<TaskState> taskStateList = getTaskStateList()
		taskStateList.remove(task)
		settings.taskListJsonString = JsonOutput.toJson(taskStateList)
	}

	public void updateTask(TaskState updatedTask) {
		if (updatedTask != null) {
			List<TaskState> taskStateList = getTaskStateList()

			for (int i = 0; i < taskStateList.size(); i++) {
				TaskState taskState = taskStateList.get(i);
				if (updatedTask.id.equals(taskState.id)) {
					taskStateList.set(i, updatedTask);
				}
			}

			settings.taskListJsonString = JsonOutput.toJson(taskStateList)
		}
	}

}
