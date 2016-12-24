package com.ideaflow.intellij.action.ifm

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.action.ActionSupport
import com.ideaflow.intellij.action.IdeaFlowToggleAction
import com.ideaflow.state.TimeConverter
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.joda.time.Duration
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent
import org.openmastery.ideaflow.intellij.settings.IdeaFlowSettings
import org.openmastery.ideaflow.intellij.settings.IdeaFlowSettingsTaskManager
import org.openmastery.publisher.api.event.EventType

@Mixin(ActionSupport)
class CreateDistractionNote extends AnAction {

	@Override
	void actionPerformed(AnActionEvent e) {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()

		Duration recentIdleDuration = controller.getRecentIdleDuration()

		if (recentIdleDuration != null) {
			String message = "You recently spent "+TimeConverter.toFormattedDuration(recentIdleDuration) + " outside of the IDE.  What was the recent distraction?"
			String distractionNote = IdeaFlowApplicationComponent.promptForInput("Label a recent distraction", message)
			controller.createEvent(distractionNote, EventType.DISTRACTION)
		} else {
			IdeaFlowApplicationComponent.showErrorMessage("No recent external activity", "Unable to find recent external activity to label as a distraction.")
		}

	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);
		disableWhenNotRecording(e)
	}

}
