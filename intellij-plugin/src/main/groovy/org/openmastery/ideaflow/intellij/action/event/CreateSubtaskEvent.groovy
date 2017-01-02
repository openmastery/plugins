package org.openmastery.ideaflow.intellij.action.event

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.openmastery.ideaflow.controller.IFMController
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent
import org.openmastery.ideaflow.intellij.action.ActionSupport
import org.openmastery.publisher.api.event.EventType

@Mixin(ActionSupport)
class CreateSubtaskEvent extends AnAction {

	@Override
	void actionPerformed(AnActionEvent e) {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()

		String subtaskNote = IdeaFlowApplicationComponent.promptForInput("Start a Subtask", "What's the major subgoal you're trying to accomplish?")
		controller.createEvent(subtaskNote, EventType.SUBTASK)
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);
		disableWhenNotRecording(e)
	}

}
