package org.openmastery.ideaflow.intellij

import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.IdeFrame
import com.intellij.ui.UIBundle
import com.intellij.util.messages.MessageBusConnection
import org.openmastery.ideaflow.controller.IFMController
import org.openmastery.ideaflow.intellij.handler.DeactivationHandler
import org.openmastery.ideaflow.intellij.handler.VirtualFileActivityHandler
import org.openmastery.ideaflow.intellij.settings.IdeaFlowSettings
import org.openmastery.ideaflow.intellij.settings.IdeaFlowSettingsTaskManager
import org.openmastery.ideaflow.state.TaskState

import javax.swing.Icon

class IdeaFlowApplicationComponent extends ApplicationComponent.Adapter {

	public static final Logger log = new Logger()

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
		controller = new IFMController(log)
		controller.setPaused(true)
		virtualFileActivityHandler = new VirtualFileActivityHandler(controller.activityHandler)

		initIfmController(IdeaFlowSettings.getInstance())

		ApplicationListener applicationListener = new ApplicationListener(controller)
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
		List<TaskState> recentTasks = taskManager.getRecentTasks()
		// TODO: should probably record the active task in settings and set it to that...
		if (recentTasks.isEmpty() == false) {
			controller.setActiveTask(recentTasks.first())
		}
	}

	@Override
	void disposeComponent() {
		controller.shutdown()
		appConnection.disconnect()
	}

	private static class ApplicationListener extends ApplicationActivationListener.Adapter {

		private DeactivationHandler deactivationHandler

		ApplicationListener(IFMController controller) {
			deactivationHandler = new DeactivationHandler(controller)
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

}
