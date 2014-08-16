package com.ideaflow.intellij.action

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.IdeaFlowApplicationComponent
import com.ideaflow.intellij.file.IdeaFlowMapFileType
import com.ideaflow.model.BandStart
import com.ideaflow.model.BandType
import com.ideaflow.model.Conflict
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.vfs.VirtualFile

class ActionSupport {

    private void disableWhenNoIdeaFlow(AnActionEvent e) {
        Presentation presentation = e.getPresentation()
        presentation.setEnabled(isIdeaFlowOpenAndNotPaused(e));
    }

    private void disableWhenOpenIdeaFlow(AnActionEvent e) {
        Presentation presentation = e.getPresentation ()
        presentation.setEnabled(!isIdeaFlowOpen(e));
    }

	private boolean isIdeaFlowOpenAndNotPaused(AnActionEvent e) {
		isIdeaFlowOpen(e) && !isPaused(e)
	}

	private IFMController getIFMController(AnActionEvent e) {
		IFMController controller = null
		if (e?.project != null) {
			controller = IdeaFlowApplicationComponent.getIFMController()
		}
		return controller
	}

	private String getActiveIdeaFlowName(AnActionEvent e) {
		getIFMController(e)?.activeIdeaFlowName
	}

    private boolean isIdeaFlowOpen(AnActionEvent e) {
	    getIFMController(e)?.isIdeaFlowOpen()
    }

    private boolean isIdeaFlowClosed(AnActionEvent e) {
        return !isIdeaFlowOpen(e)
    }

	private BandStart getActiveBandStart(AnActionEvent e) {
		getIFMController(e)?.getActiveBandStart()
	}

	private boolean isOpenBand(AnActionEvent e) {
		getIFMController(e)?.isOpenBand()
	}

	private BandType getActiveBandStartType(AnActionEvent e) {
		BandStart activeBandStart = getIFMController(e)?.activeBandStart
		activeBandStart?.type
	}

	private Conflict getActiveConflict(AnActionEvent e) {
		getIFMController(e)?.getActiveConflict()
	}

    private boolean isOpenConflict(AnActionEvent e) {
	    getIFMController(e)?.isOpenConflict()
    }

    private boolean isNotPaused(AnActionEvent e) {
        !isPaused(e)
    }

    private boolean isPaused(AnActionEvent e) {
	    getIFMController(e)?.isPaused()
    }

	private VirtualFile getSelectedIdeaFlowMapFile(AnActionEvent event) {
		VirtualFile[] selectedFiles = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(event.dataContext)

		selectedFiles.find { VirtualFile file ->
			isIdeaFlowMap(file)
		}
	}

	private boolean isIdeaFlowMap(VirtualFile file) {
		return IdeaFlowMapFileType.IFM_EXTENSION.equalsIgnoreCase(file.extension)
	}

}
