package com.ideaflow.controller

import com.ideaflow.model.Task

class IFMWorkingSet {

	private Task activeTask
	private List<Task> tasks = []
	private List<IFMWorkingSetListener> listeners = []

	void addWorkingSetListener(IFMWorkingSetListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener)
		}
	}

	void removeWorkingSetListener(IFMWorkingSetListener listener) {
		listeners.remove(listener)
	}

	private void notifyListeners() {
		listeners.each { IFMWorkingSetListener listener ->
			listener.onWorkingSetChanged()
		}
	}

	List<File> getTasks() {
		tasks.clone() as List
	}

	void setTasks(List<Task> tasks) {
		this.tasks.clear()
		this.tasks.addAll(tasks)
	}

	void setActiveTask(Task task) {
		if (activeTask != task) {
			if (!tasks.contains(task)) {
				tasks.add(task)
			}
			notifyListeners()
		}
	}

	void removeTask(Task task) {
		if (tasks.remove(task)) {
			if (task == activeTask) {
				activeTask = null
			}
			notifyListeners()
		}
	}

	boolean isEmpty() {
		tasks.isEmpty()
	}
}
