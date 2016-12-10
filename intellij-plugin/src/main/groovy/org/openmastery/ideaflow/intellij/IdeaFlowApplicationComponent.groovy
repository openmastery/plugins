package org.openmastery.ideaflow.intellij

import com.ideaflow.activity.ActivityHandler
import com.ideaflow.controller.IFMController
import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.IdeFrame
import com.intellij.ui.UIBundle
import com.intellij.util.messages.MessageBusConnection
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.format.PeriodFormatter
import org.joda.time.format.PeriodFormatterBuilder
import org.openmastery.ideaflow.intellij.file.VirtualFileActivityHandler
import org.openmastery.ideaflow.intellij.settings.IdeaFlowSettings
import org.openmastery.ideaflow.intellij.settings.IdeaFlowSettingsTaskManager
import org.openmastery.publisher.api.task.Task

import javax.swing.Icon

class IdeaFlowApplicationComponent extends ApplicationComponent.Adapter {

	private static final Logger log = Logger.getInstance(IdeaFlowApplicationComponent)

	static String NAME = "IdeaFlow.Component"

	private IFMController controller
	private MessageBusConnection appConnection
	private VirtualFileActivityHandler virtualFileActivityHandler

	static IdeaFlowApplicationComponent getApplicationComponent() {
		ApplicationManager.getApplication().getComponent(NAME) as IdeaFlowApplicationComponent
	}

	// NOTE: this accessor must not be named the same as the variable it's accessing, otherwise stackoverflow
	static IFMController getIFMController() {
		ApplicationManager.getApplication().getComponent(NAME).controller
	}

	// NOTE: this accessor must not be named the same as the variable it's accessing, otherwise stackoverflow
	static VirtualFileActivityHandler getFileActivityHandler() {
		ApplicationManager.getApplication().getComponent(NAME).virtualFileActivityHandler
	}

	static Icon getIcon(String path) {
		IconLoader.getIcon("/icons/${path}", IdeaFlowApplicationComponent.class)
	}

	static String promptForInput(String title, String message) {
		String note = Messages.showInputDialog(message, UIBundle.message(title), Messages.getQuestionIcon());
		return note
	}

	static void showErrorMessage(String title, String message) {
		Messages.showErrorDialog(message, title)
	}


	@Override
	String getComponentName() {
		NAME
	}

	@Override
	void initComponent() {
		controller = new IFMController()
		controller.setPaused(true)
		virtualFileActivityHandler = new VirtualFileActivityHandler(controller.activityHandler)

		initIfmController(IdeaFlowSettings.getInstance())

		ApplicationListener applicationListener = new ApplicationListener(controller.activityHandler)
		appConnection = ApplicationManager.getApplication().getMessageBus().connect()
		appConnection.subscribe(ApplicationActivationListener.TOPIC, applicationListener)
	}

	void initIfmController(IdeaFlowSettings settingsStore) {
		String apiUrl = settingsStore.apiUrl
		String apiKey = settingsStore.apiKey
		if ((apiUrl == null) || (apiKey == null)) {
			log.info("Disabling Idea Flow Plugin controls because API-Key or URL is unavailable {ApiUrl=$apiUrl, ApiKey=$apiKey}. " +
					         "Please fix the plugin configuration in your IDE Preferences")
			return
		}

		try {
			controller.initClients(apiUrl, apiKey)
		} catch (Exception ex) {
			// TODO: this should be a message popup to the user
			log.error("Failed to initialize controller: ${ex.message}")
		}

		IdeaFlowSettingsTaskManager taskManager = IdeaFlowSettings.instance.taskManager
		List<Task> recentTasks = taskManager.getRecentTasks()
		// TODO: should probably record the active task in settings and set it to that...
		if (recentTasks.isEmpty() == false) {
			controller.setActiveTask(recentTasks.first())
		}
	}

	@Override
	void disposeComponent() {
		appConnection.disconnect()
	}

	private static class ApplicationListener extends ApplicationActivationListener.Adapter {

		private DeactivationHandler deactivationHandler

		ApplicationListener(ActivityHandler activityHandler) {
			deactivationHandler = new DeactivationHandler(activityHandler)
		}







		@Override
		void applicationActivated(IdeFrame ideFrame) {
			if (ideFrame.project) {
				if (deactivationHandler.isPromptingForIdleTime() == false) {
					deactivationHandler.markActiveFileEventAsIdleIfDeactivationThresholdExceeded(ideFrame.project)
				}
			}
		}

		@Override
		void applicationDeactivated(IdeFrame ideFrame) {
			if (ideFrame.project) {
				deactivationHandler.deactivated()
			}
		}

	}

	private static class DeactivationHandler {

		private static final String IDLE_TITLE = "Idle Time?"

		private static final Duration DEACTIVATION_THRESHOLD = Duration.standardMinutes(50)
		private static final Duration AUTO_IDLE_THRESHOLD = Duration.standardHours(8)

		private DateTime deactivatedAt
		private boolean promptingForIdleTime
		private ActivityHandler activityHandler

		DeactivationHandler(ActivityHandler activityHandler) {
			this.activityHandler = activityHandler
		}

		boolean isPromptingForIdleTime() {
			promptingForIdleTime
		}

		void deactivated() {
			deactivatedAt = DateTime.now()
		}

		void markActiveFileEventAsIdleIfDeactivationThresholdExceeded(Project project) {
			if (getIFMController().isRecording() == false) {
				deactivatedAt = null
			}

			Duration deactivationDuration = getDeactivationDuration()
			if (!getIFMController().isTaskActive() || !deactivationDuration) {
				return
			}

			promptingForIdleTime = true
			try {
				if (deactivationDuration.isLongerThan(AUTO_IDLE_THRESHOLD)) {
					activityHandler.markIdleTime(deactivationDuration)
				} else if (deactivationDuration.isLongerThan(DEACTIVATION_THRESHOLD)) {
					boolean wasIdleTime = wasDeactivationIdleTime(project, deactivationDuration)
					if (wasIdleTime) {
						activityHandler.markIdleTime(deactivationDuration)
					} else {
						String comment = promptForInput("External Activity Comment", "What were you doing?")
						activityHandler.markExternalActivity(deactivationDuration, comment)
					}
				} else {
					activityHandler.markExternalActivity(deactivationDuration, null)
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
			StringBuilder messageBuilder = new StringBuilder()
			messageBuilder.append("Were you working ")
			String activeTaskName = getIFMController().getActiveTaskName()
			if (activeTaskName != null) {
				messageBuilder.append("on ").append(activeTaskName).append(" ")
			}
			messageBuilder.append("during the last ${formattedPeriod}?")
			int result = Messages.showYesNoDialog(project, messageBuilder.toString(), IDLE_TITLE, null)
			return result != 0
		}

	}

}
