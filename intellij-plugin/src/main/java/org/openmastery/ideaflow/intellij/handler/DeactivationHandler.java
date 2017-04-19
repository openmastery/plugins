package org.openmastery.ideaflow.intellij.handler;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.openmastery.ideaflow.activity.ActivityHandler;
import org.openmastery.ideaflow.controller.IFMController;
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent;
import org.openmastery.ideaflow.state.TimeConverter;

public class DeactivationHandler {

	private static final String IDLE_TITLE = "Idle Time?";

	private static final Duration DEACTIVATION_THRESHOLD = Duration.standardMinutes(50);
	private static final Duration AUTO_IDLE_THRESHOLD = Duration.standardHours(8);

	private IFMController controller;
	private ActivityHandler activityHandler;
	private DateTime deactivatedAt;
	private boolean promptingForIdleTime;

	public DeactivationHandler(IFMController controller) {
		this.controller = controller;
		this.activityHandler = controller.getActivityHandler();
	}

	public boolean isPromptingForIdleTime() {
		return promptingForIdleTime;
	}

	public void deactivated() {
		deactivatedAt = DateTime.now();
	}

	public void markActiveFileEventAsIdleIfDeactivationThresholdExceeded(Project project) {
		if (controller.isRecording() == false) {
			deactivatedAt = null;
		}

		Duration deactivationDuration = getDeactivationDuration();
		if (!controller.isTaskActive() || (deactivationDuration == null)) {
			return;
		}

		promptingForIdleTime = true;
		try {
			if (deactivationDuration.isLongerThan(AUTO_IDLE_THRESHOLD)) {
				activityHandler.markIdleTime(deactivationDuration);
			} else if (deactivationDuration.isLongerThan(DEACTIVATION_THRESHOLD)) {
				boolean wasIdleTime = wasDeactivationIdleTime(project, deactivationDuration);
				if (wasIdleTime) {
					activityHandler.markIdleTime(deactivationDuration);
				} else {
					String comment = IdeaFlowApplicationComponent.promptForInput("External Activity Comment", "What were you doing?");
					activityHandler.markExternalActivity(deactivationDuration, comment);
				}
			} else {
				activityHandler.markExternalActivity(deactivationDuration, null);
			}
		} finally {
			deactivatedAt = null;
			promptingForIdleTime = false;
		}
	}

	private Duration getDeactivationDuration() {
		Duration deactivationDuration = null;

		if (deactivatedAt != null) {
			long deactivationLength = DateTime.now().getMillis() - deactivatedAt.getMillis();
			deactivationDuration = Duration.millis(deactivationLength);
		}
		return deactivationDuration;
	}

	private boolean wasDeactivationIdleTime(Project project, Duration deactivationDuration) {
		String formattedPeriod = TimeConverter.toFormattedDuration(deactivationDuration);
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append("Were you working ");
		String activeTaskName = controller.getActiveTaskName();
		if (activeTaskName != null) {
			messageBuilder.append("on ").append(activeTaskName).append(" ");
		}
		messageBuilder.append("during the last " + formattedPeriod + "?");
		int result = Messages.showYesNoDialog(project, messageBuilder.toString(), IDLE_TITLE, null);
		return result != 0;
	}

}
