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
		String durationStr = TimeConverter.toFormattedDuration(recentIdleDuration)

		String message = "You recently spent $durationStr on external activity.  What was the recent distraction?"

		String timeEstimate = IdeaFlowApplicationComponent.promptForInput("Recent Distraction", message)
		controller.createEvent(timeEstimate, EventType.DISTRACTION)

	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);
		disableWhenNotRecording(e)
	}

}
