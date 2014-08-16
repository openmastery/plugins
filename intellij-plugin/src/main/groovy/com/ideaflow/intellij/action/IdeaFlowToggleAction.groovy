package com.ideaflow.intellij.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.ToggleAction

@Mixin(ActionSupport)
abstract class IdeaFlowToggleAction extends ToggleAction {

	protected abstract boolean isPresentationEnabled(AnActionEvent e)
	protected abstract String getPresentationText(AnActionEvent e)
	protected abstract String getPresentationDescription(AnActionEvent e)

	@Override
	public final void update(AnActionEvent e) {
		super.update(e);

		Presentation presentation = e.getPresentation()
		presentation.enabled = isPresentationEnabled(e)
		presentation.text = getPresentationText(e)
		 presentation.description = isIdeaFlowOpen(e) ? getPresentationDescription(e) : presentation.text
	}

}
