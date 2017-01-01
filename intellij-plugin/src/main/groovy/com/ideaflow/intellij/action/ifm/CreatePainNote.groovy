package com.ideaflow.intellij.action.ifm

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.action.ActionSupport
import com.ideaflow.state.TaskState
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent
import org.openmastery.ideaflow.intellij.settings.IdeaFlowSettings
import org.openmastery.ideaflow.intellij.settings.IdeaFlowSettingsTaskManager

import javax.swing.Icon

@Mixin(ActionSupport)
class CreatePainNote extends AnAction {

	Icon PAIN_ICON
	Icon PAIN_ICON_DOT1
	Icon PAIN_ICON_DOT2
	Icon PAIN_ICON_DOT3


	CreatePainNote() {
		PAIN_ICON = IdeaFlowApplicationComponent.getIcon("pain.png")
		PAIN_ICON_DOT1 = IdeaFlowApplicationComponent.getIcon("pain_1dot.png")
		PAIN_ICON_DOT2 = IdeaFlowApplicationComponent.getIcon("pain_2dot.png")
		PAIN_ICON_DOT3 = IdeaFlowApplicationComponent.getIcon("pain_3dot.png")
	}

	@Override
	void actionPerformed(AnActionEvent e) {
		String painMessage = promptForInput()
		if (painMessage != null) {
			IFMController controller = IdeaFlowApplicationComponent.getIFMController()

			String snippet = getSelectedText(e);
			if (snippet != null) {
				controller.createPainSnippet(painMessage, null, snippet);
			} else {
				controller.createPain(painMessage);
			}

			getTaskManager().updateTask(controller.getActiveTask())
		}
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);
		disableWhenNotRecording(e)

		IFMController controller = IdeaFlowApplicationComponent.getIFMController()
		if (controller != null && controller.getActiveTask() != null) {
			TaskState activeTask = controller.getActiveTask()
			updateIcon(e.presentation, activeTask.getUnresolvedPainList())
		}
	}

	protected String promptForInput() {
		String questionToAsk = determineQuestionToAsk()

		IdeaFlowApplicationComponent.promptForInput("WTF?!", questionToAsk)
	}

	private String determineQuestionToAsk() {
		String questionToAsk
		int painSize = 0

		IFMController controller = IdeaFlowApplicationComponent.getIFMController()
		if (controller != null && controller.getActiveTask() != null) {
			TaskState activeTask = controller.getActiveTask()
			painSize = activeTask.getUnresolvedPainList().size()
		}
		if (painSize == 0) {
			questionToAsk = "What are you confused about? (question)"
		} else {
			questionToAsk = "What are you still confused about? (discovery + question)"
		}
		return questionToAsk
	}

	private void updateIcon(Presentation presentation, List<String> unresolvedWtfs) {

		if (unresolvedWtfs == null || unresolvedWtfs.isEmpty()) {
			presentation.setIcon(PAIN_ICON)
		} else if (unresolvedWtfs.size() == 1) {
			presentation.setIcon(PAIN_ICON_DOT1)
		} else if (unresolvedWtfs.size() == 2) {
			presentation.setIcon(PAIN_ICON_DOT2)
		} else if (unresolvedWtfs.size() >= 3) {
			presentation.setIcon(PAIN_ICON_DOT3)
		}
	}

	protected static IdeaFlowSettingsTaskManager getTaskManager() {
		IdeaFlowSettings.instance.taskManager
	}

}
