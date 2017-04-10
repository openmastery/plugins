package org.openmastery.ideaflow.intellij.action.event;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.openmastery.ideaflow.controller.IFMController;
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent;
import org.openmastery.publisher.api.event.EventType;

import static org.openmastery.ideaflow.intellij.action.ActionSupport.disableWhenNotRecording;
import static org.openmastery.ideaflow.intellij.action.ActionSupport.getIFMController;

public class CreateProgressEvent extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent e) {
		IFMController controller = getIFMController(e);
		if (controller != null) {
			String progressNote = IdeaFlowApplicationComponent.promptForInput("Create a Progress Note", "What are you doing next?");
			controller.createEvent(progressNote, EventType.NOTE);
		}
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);
		disableWhenNotRecording(e);
	}

}
