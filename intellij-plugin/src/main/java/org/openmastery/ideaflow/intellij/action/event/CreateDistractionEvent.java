package org.openmastery.ideaflow.intellij.action.event;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.joda.time.Duration;
import org.openmastery.ideaflow.controller.IFMController;
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent;
import org.openmastery.ideaflow.state.TimeConverter;
import org.openmastery.publisher.api.event.EventType;

import static org.openmastery.ideaflow.intellij.action.ActionSupport.disableWhenNotRecording;
import static org.openmastery.ideaflow.intellij.action.ActionSupport.getIFMController;

public class CreateDistractionEvent extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent e) {
		IFMController controller = getIFMController(e);
		if (controller != null) {
			Duration recentIdleDuration = controller.getRecentIdleDuration();
			String durationStr = TimeConverter.toFormattedDuration(recentIdleDuration);

			String message = "You recently spent " + durationStr + " on external activity.  \nWhat was the recent distraction?";

			String timeEstimate = IdeaFlowApplicationComponent.promptForInput("Recent Distraction", message);
			controller.createEvent(timeEstimate, EventType.DISTRACTION);
		}
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);
		disableWhenNotRecording(e);
	}

}
