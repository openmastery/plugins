package com.ideaflow.intellij.action.ifm

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.IdeaFlowApplicationComponent
import com.ideaflow.intellij.action.ActionSupport
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

@Mixin(ActionSupport)
class CreateEventNoteAction extends AnAction {

	@Override
	void actionPerformed(AnActionEvent e) {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()

		String note = controller.promptForInput(e.project, "Create Note", "Enter an IdeaFlow event note:")
		controller.addNote(e.project, note)
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);
		disableWhenNoIdeaFlow(e)
	}
}
