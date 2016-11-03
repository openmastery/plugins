package com.ideaflow.intellij.action.ifm

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.action.ActionSupport
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent

@Mixin(ActionSupport)
class CreateAwesomeNote extends AnAction {

	@Override
	void actionPerformed(AnActionEvent e) {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()

		String awesomeNote = IdeaFlowApplicationComponent.promptForInput("AWESOME!", "What did you figure out?")
		controller.createAwesome(awesomeNote)
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);
		disableWhenNotRecording(e)
	}

}
