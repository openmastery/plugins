package org.openmastery.ideaflow.intellij.settings

import groovy.json.JsonException
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.openmastery.publisher.api.task.Task

/**
 * TODO: JsonOutput and LocalDateTime apparently do not mix... throws stack overflow if a Task is serialized to json
 * find a more maintainable way to persist this data!!!
 */
public class IdeaFlowSettingsTaskManager {

	private IdeaFlowSettings settings;

	public IdeaFlowSettingsTaskManager(IdeaFlowSettings settings) {
		this.settings = settings;
	}

	List<Task> getRecentTasks() {
		List<Map> recentTasks = new JsonSlurper().parseText(settings.taskListJsonString ?: '[]')
		recentTasks.collect { new Task(it) }
	}

	private List<SimpleTask> getSimpleTasks() {
		try {
			new JsonSlurper().parseText(settings.taskListJsonString ?: '[]') as List<SimpleTask>
		} catch (JsonException ex) {
			// TODO: log to intellij
			ex.printStackTrace()
			[]
		}
	}

	public void addRecentTask(Task task) {
		if (task != null) {
			List<SimpleTask> simpleTaskList = getSimpleTasks()

			SimpleTask simpleTask = new SimpleTask(id: task.id, name: task.name, description: task.description)
			simpleTaskList.add(0, simpleTask)

			int maxListSize = settings.recentTaskListSize
			if (simpleTaskList.size() > maxListSize) {
				simpleTaskList = simpleTaskList.subList(0, maxListSize)
			}

			settings.taskListJsonString = JsonOutput.toJson(simpleTaskList)
		}
	}

	public static final class SimpleTask {
		Long id
		String name
		String description
	}

}
