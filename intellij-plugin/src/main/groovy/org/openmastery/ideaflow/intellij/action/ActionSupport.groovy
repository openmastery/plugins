package org.openmastery.ideaflow.intellij.action

import org.openmastery.ideaflow.controller.IFMController
import org.openmastery.ideaflow.state.TaskState
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.SelectionModel
import com.intellij.openapi.vfs.VirtualFile
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent
import org.openmastery.ideaflow.intellij.handler.VirtualFileActivityHandler

class ActionSupport {

	public void disableWhenNotRecording(AnActionEvent e) {
		Presentation presentation = e.getPresentation()
		presentation.setEnabled(isTaskActiveAndRecording(e));
	}

	public boolean isTaskActiveAndRecording(AnActionEvent e) {
		isRecording(e) && isTaskActive(e)
	}

	// TODO: remove ActionEvent arg
	public IFMController getIFMController(AnActionEvent e) {
		IFMController controller = null
		if (e?.project != null) {
			controller = IdeaFlowApplicationComponent.getIFMController()
		}
		return controller
	}

	public String getActiveIdeaFlowName(AnActionEvent e) {
		getIFMController(e)?.activeTaskName
	}

	public TaskState getActiveTask(AnActionEvent e) {
		getIFMController(e)?.getActiveTask()
	}

	public boolean isTaskActive(AnActionEvent e) {
		getIFMController(e)?.isTaskActive()
	}

	public boolean isRecording(AnActionEvent e) {
		getIFMController(e)?.isRecording()
	}

	public boolean isPaused(AnActionEvent e) {
		IFMController controller = getIFMController(e)
		controller == null ? true : controller.isPaused()
	}

	public String getSelectedText(AnActionEvent e) {
		Editor editor = e.getData(CommonDataKeys.EDITOR);
		if (editor == null) {
			return null;
		}

		SelectionModel selectionModel = editor.getSelectionModel();
		return selectionModel.getSelectedText();
	}

	public String getActiveFilePath(AnActionEvent e) {
		VirtualFile file = e.getData(LangDataKeys.VIRTUAL_FILE)
		String fileName = null

		if (file != null) {
			fileName = VirtualFileActivityHandler.getFullFilePathOrDefault(file, e.getProject(), file.name)
		}
		fileName
	}

}
