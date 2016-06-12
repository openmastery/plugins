package com.ideaflow.intellij.action.ifm

import com.ideaflow.intellij.action.ActionSupport
import com.ideaflow.intellij.action.IdeaFlowToggleAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent

@Mixin(ActionSupport)
class ToggleRecord extends IdeaFlowToggleAction {

	private static final String RECORD_TITLE = "Record IdeaFlow"
	private static final String PAUSE_TITLE = "Pause IdeaFlow"

	@Override
	protected boolean isPresentationEnabled(AnActionEvent e) {
		return isTaskActive(e)
	}

	@Override
	protected String getPresentationText(AnActionEvent e) {
		return isRecording() ? PAUSE_TITLE : RECORD_TITLE
	}

	@Override
	protected String getPresentationDescription(AnActionEvent e) {
		return "${getPresentationText(e)}: ${getActiveIdeaFlowName(e)}"
	}

	@Override
	boolean isSelected(AnActionEvent e) {
		return isRecording()
	}

	@Override
	void setSelected(AnActionEvent e, boolean state) {
		IdeaFlowApplicationComponent.recording = state
	}

}
