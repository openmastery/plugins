package com.ideaflow.intellij

import com.ideaflow.controller.IFMController
import com.ideaflow.controller.IFMTaskListListener
import com.ideaflow.dsl.TaskId
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project

class IdeaFlowState {

	private static final String OPEN_TASKS = "IFM.OpenTasks"
	private static final String ACTIVE_TASK = "IFM.ActiveTask"

	private IFMController controller
	private PropertiesComponent properties
	private boolean restoringActiveState = false

	// TODO: clean this up

	IdeaFlowState(IFMController controller) {
		this.controller = controller
		properties = PropertiesComponent.getInstance()

		controller.addTaskListListener(new IFMTaskListListener() {
			@Override
			void onTaskListChanged() {
				if (restoringActiveState) {
					return
				}

				ApplicationManager.getApplication().invokeLater(new Runnable() {
					@Override
					void run() {
						saveActiveState()
					}
				})
			}
		})
	}

	public void saveActiveState() {
		saveOpenTasks()
		saveActiveTask()
	}

	public void restoreActiveState(Project project) {
		restoringActiveState = true
		try {
			List<TaskId> savedTasks = getSavedOpenTasks()
			TaskId savedTask = getSavedActiveTask()

			if (!savedTask && savedTasks) {
				savedTask = savedTasks.first()
			}

			controller.setWorkingSet(savedTasks)
			if (savedTask) {
				controller.newIdeaFlow(project, savedTask)
			}
		} finally {
			restoringActiveState = false
		}
	}

	private void saveOpenTasks() {
		List<TaskId> activeTasks = controller.getWorkingSet()
		properties.setValue(OPEN_TASKS, activeTasks.collect{it.value}.join('\n'))
	}

	private void saveActiveTask() {
		properties.setValue(ACTIVE_TASK, controller.activeIdeaFlowModel?.taskId?.value)
	}

	public List<TaskId> getSavedOpenTasks() {
		TaskId[] taskIds = properties.getValue(OPEN_TASKS)?.split('\n').collect{ new TaskId(it) }
		taskIds ? taskIds : []
	}

	public TaskId getSavedActiveTask() {
		String active = properties.getValue(ACTIVE_TASK)
		active ? new TaskId(active) : null
	}
}
