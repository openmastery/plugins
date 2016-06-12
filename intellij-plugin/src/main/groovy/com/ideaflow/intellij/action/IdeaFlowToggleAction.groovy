package com.ideaflow.intellij.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.ToggleAction
import javax.swing.Icon

@Mixin(ActionSupport)
abstract class IdeaFlowToggleAction extends ToggleAction {

	protected abstract boolean isPresentationEnabled(AnActionEvent e)

	protected abstract String getPresentationText(AnActionEvent e)

	protected abstract String getPresentationDescription(AnActionEvent e)

	protected Icon getPresentationIcon(AnActionEvent e) {
		null
	}

	@Override
	public final void update(AnActionEvent e) {
		super.update(e);

		Presentation presentation = e.getPresentation()
		presentation.enabled = isPresentationEnabled(e)
		presentation.text = getPresentationText(e)
		presentation.description = isTaskActiveAndRecording(e) ? getPresentationDescription(e) : presentation.text

		Icon icon = getPresentationIcon(e)
		if (icon != null) {
			presentation.icon = icon
		}
	}

}
