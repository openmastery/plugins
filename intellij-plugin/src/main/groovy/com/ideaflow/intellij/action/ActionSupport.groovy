package com.ideaflow.intellij.action

import com.ideaflow.controller.IFMController
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent
import org.openmastery.publisher.api.ideaflow.IdeaFlowPartialCompositeState
import org.openmastery.publisher.api.ideaflow.IdeaFlowState
import org.openmastery.publisher.api.task.Task

class ActionSupport {

	private void disableWhenNotRecording(AnActionEvent e) {
		Presentation presentation = e.getPresentation()
		presentation.setEnabled(isTaskActiveAndRecording(e));
	}

//	private void disableWhenOpenIdeaFlow(AnActionEvent e) {
//		Presentation presentation = e.getPresentation()
//		presentation.setEnabled(!isIdeaFlowOpen(e));
//	}
//
	private IdeaFlowPartialCompositeState getActiveTaskState(AnActionEvent e) {
		isRecording() ? getIFMController(e).getActiveTaskState() : null
	}

	private boolean isTaskActiveAndRecording(AnActionEvent e) {
		isRecording() && isTaskActive(e)
	}

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

	private IdeaFlowState getActiveConflict(AnActionEvent e) {
		IdeaFlowPartialCompositeState compositeState = getActiveTaskState(e)
		compositeState?.getActiveConflict()
	}


//	private boolean isIdeaFlowClosed(AnActionEvent e) {
//		return !isIdeaFlowOpen(e)
//	}
//
//	private BandStart getActiveBandStart(AnActionEvent e) {
//		getIFMController(e)?.getActiveBandStart()
//	}
//
//	private boolean isOpenBand(AnActionEvent e) {
//		getIFMController(e)?.isOpenBand()
//	}
//
//	private BandType getActiveBandStartType(AnActionEvent e) {
//		BandStart activeBandStart = getIFMController(e)?.activeBandStart
//		activeBandStart?.type
//	}
//
//	private Conflict getActiveConflict(AnActionEvent e) {
//		getIFMController(e)?.getActiveConflict()
//	}
//
//	private boolean isOpenConflict(AnActionEvent e) {
//		getIFMController(e)?.isOpenConflict()
//	}
//
//	private boolean isNotPaused(AnActionEvent e) {
//		!isPaused(e)
//	}

	private boolean isRecording() {
		IdeaFlowApplicationComponent.isRecording()
	}

//	private VirtualFile getSelectedIdeaFlowMapFile(AnActionEvent event) {
//		VirtualFile[] selectedFiles = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(event.dataContext)
//
//		selectedFiles.find { VirtualFile file ->
//			isIdeaFlowMap(file)
//		}
//	}
//
//	private boolean isIdeaFlowMap(VirtualFile file) {
//		return IdeaFlowMapFileType.IFM_EXTENSION.equalsIgnoreCase(file.extension)
//	}

}
