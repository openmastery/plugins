package org.openmastery.ideaflow.intellij.action.ifm

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.openmastery.ideaflow.controller.IFMController
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent
import org.openmastery.ideaflow.intellij.action.ActionSupport
import org.openmastery.publisher.api.event.EventType

@Mixin(ActionSupport)
class CreateProgressNote extends AnAction {

	@Override
	void actionPerformed(AnActionEvent e) {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()

		String progressNote = IdeaFlowApplicationComponent.promptForInput("Create a Progress Note", "What are you doing next?")
		controller.createEvent(progressNote, EventType.NOTE)
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);
		disableWhenNotRecording(e)
	}

}
