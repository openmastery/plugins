package org.openmastery.ideaflow.intellij.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent;
import org.openmastery.ideaflow.intellij.Logger;
import org.openmastery.ideaflow.state.TaskState;
import org.openmastery.ideaflow.state.TaskStateJsonMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IdeaFlowSettingsTaskManager {

	private TaskStateJsonMapper taskStateJsonMapper = new TaskStateJsonMapper();
	private IdeaFlowSettings settings;
	private Logger logger;

	public IdeaFlowSettingsTaskManager(IdeaFlowSettings settings) {
		this.settings = settings;
		this.logger = IdeaFlowApplicationComponent.log;
	}

	public List<TaskState> getRecentTasks() {
		String taskListJsonString = settings.getTaskListJsonString();
		try {
			return taskStateJsonMapper.toList(taskListJsonString);
		} catch (IOException ex) {
			logger.error("JSON error while attempting to convert task list=" + taskListJsonString + ", clearing out task list");
			return new ArrayList<>();
		}
	}

	private void persistTaskListAsJsonString(List<TaskState> taskStateList) {
		try {
			settings.setTaskListJsonString(taskStateJsonMapper.toJson(taskStateList));
		} catch (JsonProcessingException e) {
			logger.error("JSON error while attempting to convert task list to json string, task list will not be updated");
		}
	}

	public void addRecentTask(TaskState task) {
		if (task != null) {
			List<TaskState> taskStateList = getRecentTasks();

			taskStateList.add(0, task);

			int maxListSize = settings.getRecentTaskListSize();
			if (taskStateList.size() > maxListSize) {
				taskStateList = taskStateList.subList(0, maxListSize);
			}

			persistTaskListAsJsonString(taskStateList);
		}
	}

	public void removeTask(TaskState task) {
		List<TaskState> taskStateList = getRecentTasks();
		taskStateList.remove(task);
		persistTaskListAsJsonString(taskStateList);
	}

	public void updateTask(TaskState updatedTask) {
		if (updatedTask != null) {
			List<TaskState> taskStateList = getRecentTasks();

			for (int i = 0; i < taskStateList.size(); i++) {
				TaskState taskState = taskStateList.get(i);
				if (updatedTask.getId().equals(taskState.getId())) {
					taskStateList.set(i, updatedTask);
				}
			}

			persistTaskListAsJsonString(taskStateList);
		}
	}

}
