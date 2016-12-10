package org.openmastery.ideaflow.intellij.settings

import com.ideaflow.state.TaskState
import groovy.json.JsonException
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

/**
 * TODO: JsonOutput and LocalDateTime apparently do not mix... throws stack overflow if a Task is serialized to json
 * find a more maintainable way to persist this data!!!
 */
public class IdeaFlowSettingsTaskManager {

	private IdeaFlowSettings settings;

	public IdeaFlowSettingsTaskManager(IdeaFlowSettings settings) {
		this.settings = settings;
	}

	List<TaskState> getRecentTasks() {
		List<Map> recentTasks = new JsonSlurper().parseText(settings.taskListJsonString ?: '[]')
		recentTasks.collect { new TaskState(it) }
	}


	private List<TaskState> getTaskStateList() {
		try {
			new JsonSlurper().parseText(settings.taskListJsonString ?: '[]') as List<TaskState>
		} catch (JsonException ex) {
			// TODO: log to intellij
			ex.printStackTrace()
			[]
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
