package com.ideaflow.intellij.action.ifm

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.action.ActionSupport
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent
import org.openmastery.publisher.api.event.EventType

@Mixin(ActionSupport)
class CreateProgressNote extends AnAction {

	@Override
	void actionPerformed(AnActionEvent e) {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()

		String subtaskNote = IdeaFlowApplicationComponent.promptForInput("Create a Progress Note", "What are you doing next? (micro-decision)")
		controller.createEvent(subtaskNote, EventType.SUBTASK)
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);
		disableWhenNotRecording(e)
	}

}
