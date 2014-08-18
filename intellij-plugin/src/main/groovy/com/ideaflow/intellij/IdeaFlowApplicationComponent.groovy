package com.ideaflow.intellij

import com.ideaflow.controller.IDEService
import com.ideaflow.controller.IFMController
import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.IdeFrame
import com.intellij.util.messages.MessageBusConnection
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.Period
import org.joda.time.format.PeriodFormatter
import org.joda.time.format.PeriodFormatterBuilder

class IdeaFlowApplicationComponent implements ApplicationComponent {

	static String NAME = "IdeaFlow.Component"

	private IDEService<Project> ideService
	private IFMController<Project> controller
	private MessageBusConnection appConnection

	static IFMController<Project> getIFMController() {
		ApplicationManager.getApplication().getComponent(NAME).controller
	}

	@Override
	String getComponentName() {
		NAME
	}

	@Override
	void initComponent() {
		ideService = new IDEServiceImpl()
		controller = new IFMController(ideService)

		ApplicationListener applicationListener = new ApplicationListener()
		appConnection = ApplicationManager.getApplication().getMessageBus().connect()
		appConnection.subscribe(ApplicationActivationListener.TOPIC, applicationListener)
	}

	@Override
	void disposeComponent() {
		appConnection.disconnect()
	}


	private static class ApplicationListener implements ApplicationActivationListener {

		private DeactivationHandler deactivationHandler = new DeactivationHandler()

		void applicationActivated(IdeFrame ideFrame) {
			if (ideFrame.project) {
				if (!deactivationHandler.isPromptingForIdleTime()) {
					deactivationHandler.markActiveFileEventAsIdleIfDeactivationThresholdExceeded(ideFrame.project)
					getIFMController().startFileEventForCurrentFile(ideFrame.project)
				}
			}
		}

		void applicationDeactivated(IdeFrame ideFrame) {
			if (ideFrame.project) {
				deactivationHandler.deactivated()
				getIFMController().startFileEvent(ideFrame.project, "[[deactivated]]")
			}
		}

	}

	private static class DeactivationHandler {

		private static final String IDLE_TITLE = "Idle Time?"
		private static final String IDLE_QUESTION_MESSAGE = "What were you doing?"

		private static final Duration DEACTIVATION_THRESHOLD = Duration.standardMinutes(50)
		private static final Duration AUTO_IDLE_THRESHOLD = Duration.standardHours(12)

		private DateTime deactivatedAt
		private boolean promptingForIdleTime

		boolean isPromptingForIdleTime() {
			promptingForIdleTime
		}

		void deactivated() {
			deactivatedAt = DateTime.now()
		}

		void markActiveFileEventAsIdleIfDeactivationThresholdExceeded(Project project) {
			Duration deactivationDuration = getDeactivationDuration()
			if (!getIFMController().isIdeaFlowOpen() || !deactivationDuration) {
				return
			}

			promptingForIdleTime = true
			try {
				if (deactivationDuration.isLongerThan(AUTO_IDLE_THRESHOLD)) {
					getIFMController().markActiveFileEventAsIdle("[Auto Idle]")
				} else if (deactivationDuration.isLongerThan(DEACTIVATION_THRESHOLD)) {
					if (wasDeactivationIdleTime(project, deactivationDuration)) {
						String comment = getIFMController().promptForInput(project, IDLE_TITLE, IDLE_QUESTION_MESSAGE)
						getIFMController().markActiveFileEventAsIdle(comment)
					}
				}
			} finally {
				deactivatedAt = null
				promptingForIdleTime = false
			}
		}

		private Duration getDeactivationDuration() {
			Duration deactivationDuration = null

			if (deactivatedAt) {
				long deactivationLength = DateTime.now().millis - deactivatedAt.millis
				deactivationDuration = Duration.millis(deactivationLength)
			}
			deactivationDuration
		}

		private boolean wasDeactivationIdleTime(Project project, Duration deactivationDuration) {
			PeriodFormatter formatter = new PeriodFormatterBuilder()
					.appendDays()
					.appendSuffix("d")
					.appendHours()
					.appendSuffix("h")
					.appendMinutes()
					.appendSuffix("m")
					.toFormatter()

			String formattedPeriod = formatter.print(deactivationDuration.toPeriod())
			String message = "Were you working during the last ${formattedPeriod}?"
			int result = Messages.showYesNoDialog(project, message, IDLE_TITLE, null)
			return result != 0
		}
	}

}
