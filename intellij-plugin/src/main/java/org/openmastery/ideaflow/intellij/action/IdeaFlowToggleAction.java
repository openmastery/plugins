package org.openmastery.ideaflow.intellij.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ToggleAction;

import javax.swing.Icon;

import static org.openmastery.ideaflow.intellij.action.ActionSupport.isTaskActiveAndRecording;

public abstract class IdeaFlowToggleAction extends ToggleAction {

	protected abstract boolean isPresentationEnabled(AnActionEvent e);

	protected abstract String getPresentationText(AnActionEvent e);

	protected abstract String getPresentationDescription(AnActionEvent e);

	protected Icon getPresentationIcon(AnActionEvent e) {
		return null;
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);

		Presentation presentation = e.getPresentation();
		presentation.setEnabled(isPresentationEnabled(e));
		presentation.setText(getPresentationText(e));
		presentation.setDescription(getDescription(e, presentation));

		Icon icon = getPresentationIcon(e);
		if (icon != null) {
			presentation.setIcon(icon);
		}
	}

	private String getDescription(AnActionEvent e, Presentation presentation) {
		return isTaskActiveAndRecording(e) ? getPresentationDescription(e) : presentation.getText();
	}

}
