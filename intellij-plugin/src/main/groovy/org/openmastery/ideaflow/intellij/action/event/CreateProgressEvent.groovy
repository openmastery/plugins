package org.openmastery.ideaflow.intellij.action.event

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.openmastery.ideaflow.controller.IFMController
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent
import org.openmastery.ideaflow.intellij.action.ActionSupport
import org.openmastery.publisher.api.event.EventType

@Mixin(ActionSupport)
class CreateProgressEvent extends AnAction {

	@Override
	void actionPerformed(AnActionEvent e) {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()

		String progressNote = IdeaFlowApplicationComponent.promptForInput("Start a Progress Tick", "What are you doing next?")
		controller.createEvent(progressNote, EventType.NOTE)
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);
		disableWhenNotRecording(e)
	}

}
