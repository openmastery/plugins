package org.openmastery.ideaflow.intellij.action.ifm

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.openmastery.ideaflow.controller.IFMController
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent
import org.openmastery.ideaflow.intellij.action.ActionSupport
import org.openmastery.ideaflow.intellij.settings.IdeaFlowSettings
import org.openmastery.ideaflow.intellij.settings.IdeaFlowSettingsTaskManager

@Mixin(ActionSupport)
class CreateAwesomeNote extends AnAction {

	@Override
	void actionPerformed(AnActionEvent e) {
		String awesomeMessage = promptForInput()

		if (awesomeMessage != null) {
			IFMController controller = IdeaFlowApplicationComponent.getIFMController()

			String snippet = getSelectedText(e)
			if (snippet == null) {
				controller.resolveWithYay(awesomeMessage)
			} else {
				String source = getActiveFilePath(e)
				controller.resolveWithAwesomeSnippet(awesomeMessage, source, snippet);
			}

			getTaskManager().updateTask(controller.getActiveTask())
		}
	}

	protected String promptForInput() {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()

		List<String> unresolvedPainList = controller.getActiveTask().getUnresolvedPainList()

		String wtfString = "";
		for (int i = 0; i < unresolvedPainList.size(); i++) {
			String wtfMessage = unresolvedPainList.get(i)
			wtfString += "-- $i: " + wtfMessage + "\n"
		}

		IdeaFlowApplicationComponent.promptForInput("YAY!", "What did you figure out? (discovery)\n" + wtfString)
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
