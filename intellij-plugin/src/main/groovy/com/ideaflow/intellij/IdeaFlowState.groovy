package com.ideaflow.intellij

import com.ideaflow.controller.IFMController
import com.ideaflow.controller.IFMWorkingSetListener
import com.ideaflow.intellij.settings.IdeaSettingsService
import com.ideaflow.model.Task
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project

class IdeaFlowState {

	private IFMController controller
	private IdeaSettingsService storage
	private boolean restoringActiveState = false

	// TODO: clean this up

	IdeaFlowState(IFMController controller) {
		this.controller = controller
		this.storage = new IdeaSettingsService()

		controller.addWorkingSetListener(new IFMWorkingSetListener() {
			@Override
			void onWorkingSetChanged() {
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
		saveOpenFiles()
		saveActiveFile()
	}

	public void restoreActiveState(Project project) {
		restoringActiveState = true
		try {
			List<Task> savedOpenTasks = storage.loadOpenTasks()
			Task savedActiveTask = storage.loadActiveTask()

			if (!savedActiveTask && savedOpenTasks) {
				savedActiveTask = savedOpenTasks.first()
			}

			controller.setWorkingSetTasks(savedOpenTasks)

			if (savedActiveTask) {
				controller.newIdeaFlow(project, savedActiveTask)
			}
		} finally {
			restoringActiveState = false
		}
	}

	private void saveOpenFiles() {
		List<Task> openTasks = controller.getWorkingSetTasks()
		storage.saveOpenTasks(openTasks)
	}

	private void saveActiveFile() {
		storage.saveActiveTask(controller.activeIdeaFlowModel?.task)
	}
}
