package com.ideaflow.intellij.action

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.IdeaFlowComponent
import com.ideaflow.intellij.file.IdeaFlowMapFileType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.vfs.VirtualFile

class ActionSupport {

    private void disableWhenNoIdeaFlow(AnActionEvent e) {
        Presentation presentation = e.getPresentation()
        boolean enable = isIdeaFlowOpen(e) && !isPaused(e)
        presentation.setEnabled(enable);
    }

    private void disableWhenOpenIdeaFlow(AnActionEvent e) {
        Presentation presentation = e.getPresentation ()
        presentation.setEnabled(!isIdeaFlowOpen(e));
    }

    private boolean isIdeaFlowOpen(AnActionEvent e) {
        boolean isOpen = false
        if (e?.project != null) {
            IFMController controller = IdeaFlowComponent.getIFMController(e.project)
            isOpen = controller.isIdeaFlowOpen()
        }
        return isOpen
    }

    private boolean isIdeaFlowClosed(AnActionEvent e) {
        return !isIdeaFlowOpen(e)
    }

    private boolean isOpenConflict(AnActionEvent e) {
        boolean isOpen = false
        if (e?.project != null) {
            IFMController controller = IdeaFlowComponent.getIFMController(e.project)
            isOpen = controller.isOpenConflict()
        }
        return isOpen
    }

    private boolean isNotPaused(AnActionEvent e) {
        !isPaused(e)
    }


    private boolean isPaused(AnActionEvent e) {
        boolean isPaused = false
        if (e?.project != null) {
            IFMController controller = IdeaFlowComponent.getIFMController(e.project)
            isPaused = controller.isPaused()
        }
        return isPaused
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
