package com.ideaflow.controller

import com.ideaflow.dsl.TaskId

class IFMTaskList {

	private TaskId activeTask
	private List<TaskId> tasks = []
	private List<IFMTaskListListener> listeners = []

	void addTasksListener(IFMTaskListListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener)
		}
	}

	/**
	 * @deprecated do we need to use this? TODO
	 * @param listener
	 */
	void removeWorkingSetListener(IFMTaskListListener listener) {
		listeners.remove(listener)
	}

	private void notifyListeners() {
		listeners.each { IFMTaskListListener listener ->
			listener.onTaskListChanged()
		}
	}

	List<TaskId> getTaskList() {
		tasks.collect(Closure.IDENTITY)
	}

	void setTaskList(List<TaskId> tasks) {
		this.tasks = tasks.collect(Closure.IDENTITY)
	}

	void setActiveTask(TaskId taskId) {
		if (activeTask != taskId) {
			if (!tasks.contains(taskId)) {
				tasks.add(taskId)
			}
			notifyListeners()
		}
	}

	void removeTask(TaskId taskId) {
		if (tasks.remove(taskId)) {
			if (taskId == activeTask) {
				activeTask = null
			}
			notifyListeners()
		}
	}

	boolean isEmpty() {
		tasks.isEmpty()
	}

}
