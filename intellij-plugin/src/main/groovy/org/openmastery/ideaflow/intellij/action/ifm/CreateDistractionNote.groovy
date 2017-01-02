package org.openmastery.ideaflow.intellij.action.ifm

import org.openmastery.ideaflow.state.TimeConverter
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.joda.time.Duration
import org.openmastery.ideaflow.controller.IFMController
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent
import org.openmastery.ideaflow.intellij.action.ActionSupport
import org.openmastery.publisher.api.event.EventType

@Mixin(ActionSupport)
class CreateDistractionNote extends AnAction {

	@Override
	void actionPerformed(AnActionEvent e) {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()

		Duration recentIdleDuration = controller.getRecentIdleDuration()
		String durationStr = TimeConverter.toFormattedDuration(recentIdleDuration)

		String message = "You recently spent $durationStr on external activity.  \nWhat was the recent distraction?"

		String timeEstimate = IdeaFlowApplicationComponent.promptForInput("Recent Distraction", message)
		controller.createEvent(timeEstimate, EventType.DISTRACTION)

	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);
		disableWhenNotRecording(e)
	}

}
