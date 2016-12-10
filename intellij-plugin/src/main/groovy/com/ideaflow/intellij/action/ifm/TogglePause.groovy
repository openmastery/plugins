package com.ideaflow.intellij.action.ifm

import com.ideaflow.intellij.action.ActionSupport
import com.ideaflow.intellij.action.IdeaFlowToggleAction
import com.intellij.openapi.actionSystem.AnActionEvent

@Mixin(ActionSupport)
class TogglePause extends IdeaFlowToggleAction {

	private static final String RECORD_TITLE = "Record IdeaFlow"
	private static final String PAUSE_TITLE = "Pause IdeaFlow"

	@Override
	protected boolean isPresentationEnabled(AnActionEvent e) {
		return isTaskActive(e)
	}

	@Override
	protected String getPresentationText(AnActionEvent e) {
		return isPaused(e) ? RECORD_TITLE : PAUSE_TITLE
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
		getIFMController(e)?.setPaused(state)
	}


}
