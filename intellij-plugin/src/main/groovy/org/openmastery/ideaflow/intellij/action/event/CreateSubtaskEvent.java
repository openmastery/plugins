package org.openmastery.ideaflow.intellij.action.event;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.openmastery.ideaflow.controller.IFMController;
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent;
import org.openmastery.publisher.api.event.EventType;

import static org.openmastery.ideaflow.intellij.action.ActionSupport.disableWhenNotRecording;
import static org.openmastery.ideaflow.intellij.action.ActionSupport.getIFMController;

public class CreateSubtaskEvent extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent e) {
		IFMController controller = getIFMController(e);
		if (controller != null) {
			String subtaskNote = IdeaFlowApplicationComponent.promptForInput("Start a Subtask", "What's the major subgoal you're trying to accomplish?");
			controller.createEvent(subtaskNote, EventType.SUBTASK);
		}
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);
		disableWhenNotRecording(e);
	}

}
