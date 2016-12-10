package com.ideaflow.intellij.action.ifm

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.action.ActionSupport
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent
import org.openmastery.ideaflow.intellij.settings.IdeaFlowSettings
import org.openmastery.ideaflow.intellij.settings.IdeaFlowSettingsTaskManager
import org.openmastery.publisher.api.event.EventType

@Mixin(ActionSupport)
class CreateAwesomeNote extends AnAction {

	@Override
	void actionPerformed(AnActionEvent e) {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()

		List<String> unresolvedWTFs = controller.getActiveTask().unresolvedWTFList

		String wtfString = "";
		for (String wtfMessage : unresolvedWTFs) {
			wtfString += "-- WTF: " + wtfMessage + "\n"
		}

		String awesomeNote = IdeaFlowApplicationComponent.promptForInput("AWESOME!", "What caused the confusion?\n" + wtfString)
		if (awesomeNote != null) {
			controller.resolveWithYay(awesomeNote)
			getTaskManager().updateTask(controller.getActiveTask())
		}
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);
		disableWhenNotRecording(e)
	}

	private static IdeaFlowSettingsTaskManager getTaskManager() {
		IdeaFlowSettings.instance.taskManager
	}
}
