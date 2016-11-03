package com.ideaflow.intellij.action

import com.ideaflow.controller.IFMController
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent
import org.openmastery.publisher.api.task.Task

class ActionSupport {

	private void disableWhenNotRecording(AnActionEvent e) {
		Presentation presentation = e.getPresentation()
		presentation.setEnabled(isTaskActiveAndRecording(e));
	}

	private boolean isTaskActiveAndRecording(AnActionEvent e) {
		isRecording(e) && isTaskActive(e)
	}

	// TODO: remove ActionEvent arg
	private IFMController getIFMController(AnActionEvent e) {
		IFMController controller = null
		if (e?.project != null) {
			controller = IdeaFlowApplicationComponent.getIFMController()
		}
		return controller
	}

	private String getActiveIdeaFlowName(AnActionEvent e) {
		getIFMController(e)?.activeTaskName
	}

	private Task getActiveTask(AnActionEvent e) {
		getIFMController(e).getActiveTask()
	}

	private boolean isTaskActive(AnActionEvent e) {
		getIFMController(e)?.isTaskActive()
	}

	private boolean isRecording(AnActionEvent e) {
		getIFMController(e)?.isRecording()
	}

	private boolean isPaused(AnActionEvent e) {
		IFMController controller = getIFMController(e)
		controller == null ? true : controller.isPaused()
	}

}
