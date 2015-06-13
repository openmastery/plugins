package com.ideaflow.intellij.action.ifm

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.IdeaFlowApplicationComponent
import com.ideaflow.intellij.action.ActionSupport
import com.ideaflow.intellij.action.IdeaFlowToggleAction
import com.intellij.openapi.actionSystem.AnActionEvent

@Mixin(ActionSupport)
class TogglePause extends IdeaFlowToggleAction {

	private static final String PAUSE_TITLE = "Pause IdeaFlow"
	private static final String RESUME_TITLE = "Resume IdeaFlow"

	@Override
	protected boolean isPresentationEnabled(AnActionEvent e) {
		return isIdeaFlowOpen(e)
	}

	@Override
	protected String getPresentationText(AnActionEvent e) {
		return isPaused(e) ? RESUME_TITLE : PAUSE_TITLE
	}

	@Override
	protected String getPresentationDescription(AnActionEvent e) {
		return "${getPresentationText(e)}: ${getActiveIdeaFlowName(e)}"
	}

	@Override
    boolean isSelected(AnActionEvent e) {
        return isPaused(e)
    }

    @Override
    void setSelected(AnActionEvent e, boolean state) {
        IFMController controller = IdeaFlowApplicationComponent.getIFMController()

        if (controller.isPaused()) {
            controller.resume(e.project)
        } else {
            controller.pause(e.project)
        }
    }

}
