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
import org.openmastery.publisher.api.event.EventType

import javax.swing.Icon

@Mixin(ActionSupport)
class CreateWTFNote extends AnAction {

	Icon WTF_ICON
	Icon WTF_ICON_DOT1
	Icon WTF_ICON_DOT2
	Icon WTF_ICON_DOT3


	CreateWTFNote() {
		WTF_ICON = IdeaFlowApplicationComponent.getIcon("pain.png")
		WTF_ICON_DOT1 = IdeaFlowApplicationComponent.getIcon("pain_1dot.png")
		WTF_ICON_DOT2 = IdeaFlowApplicationComponent.getIcon("pain_2dot.png")
		WTF_ICON_DOT3 = IdeaFlowApplicationComponent.getIcon("pain_3dot.png")
	}

	@Override
	void actionPerformed(AnActionEvent e) {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()
		String questionToAsk = determineQuestionToAsk()

		String wtfNote = IdeaFlowApplicationComponent.promptForInput("WTF?!", questionToAsk)
		if (wtfNote != null) {
			controller.createWTF(wtfNote)
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
			updateIcon(e.presentation, activeTask.getUnresolvedWTFList())
		}

	}

	private String determineQuestionToAsk() {
		String questionToAsk
		int wtfSize = 0
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()
		if (controller != null && controller.getActiveTask() != null) {
			TaskState activeTask = controller.getActiveTask()
			wtfSize = activeTask.getUnresolvedWTFList().size()
		}
		if (wtfSize == 0) {
			questionToAsk = "What are you confused about? (question)"
		} else {
			questionToAsk = "What did you discover? What are you still confused about?"
		}
		return questionToAsk
	}

	private void updateIcon(Presentation presentation, List<String> unresolvedWtfs) {
		if (unresolvedWtfs == null || unresolvedWtfs.isEmpty()) {
			presentation.setIcon(WTF_ICON)
		} else if (unresolvedWtfs.size() == 1) {
			presentation.setIcon(WTF_ICON_DOT1)
		} else if (unresolvedWtfs.size() == 2) {
			println "Icon 2!"
			presentation.setIcon(WTF_ICON_DOT2)
		} else if (unresolvedWtfs.size() >= 3) {
			println "Icon 3!"
			presentation.setIcon(WTF_ICON_DOT3)
			presentation.setText("Yo!")

		}

	}

	private static IdeaFlowSettingsTaskManager getTaskManager() {
		IdeaFlowSettings.instance.taskManager
	}

}
