package com.ideaflow.intellij.action.ifm

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.action.ActionSupport
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent
import org.openmastery.publisher.api.event.EventType

@Mixin(ActionSupport)
class CreateSubtaskNote extends AnAction {

	@Override
	void actionPerformed(AnActionEvent e) {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()

		String subtaskNote = IdeaFlowApplicationComponent.promptForInput("Start a Subtask", "What subtask are you doing next?")
		controller.createEvent(subtaskNote, EventType.SUBTASK)
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);
		disableWhenNotRecording(e)
	}

}
