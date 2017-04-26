package org.openmastery.ideaflow.intellij;

import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.ui.UIBundle;
import com.intellij.util.messages.MessageBusConnection;
import org.openmastery.ideaflow.controller.IFMController;
import org.openmastery.ideaflow.intellij.handler.DeactivationHandler;
import org.openmastery.ideaflow.intellij.handler.VirtualFileActivityHandler;
import org.openmastery.ideaflow.intellij.settings.IdeaFlowSettings;
import org.openmastery.ideaflow.intellij.settings.IdeaFlowSettingsTaskManager;
import org.openmastery.ideaflow.state.TaskState;

import javax.swing.Icon;
import java.util.List;

public class IdeaFlowApplicationComponent extends ApplicationComponent.Adapter {

	public static final Logger log = new Logger();

	private static final String NAME = "IdeaFlow.Component";

	private IFMController controller;
	private MessageBusConnection appConnection;
	private VirtualFileActivityHandler virtualFileActivityHandler;

	public static IdeaFlowApplicationComponent getApplicationComponent() {
		return (IdeaFlowApplicationComponent) ApplicationManager.getApplication().getComponent(NAME);
	}

	public static IFMController getIFMController() {
		return getApplicationComponent().controller;
	}

	public static VirtualFileActivityHandler getFileActivityHandler() {
		return getApplicationComponent().virtualFileActivityHandler;
	}

	public static Icon getIcon(String path) {
		return IconLoader.getIcon("/icons/" + path, IdeaFlowApplicationComponent.class);
	}

	public static String promptForInput(String title, String message) {
		return Messages.showInputDialog(message, UIBundle.message(title), Messages.getQuestionIcon());
	}

	public static void showErrorMessage(String title, String message) {
		Messages.showErrorDialog(message, title);
	}


	@Override
	public String getComponentName() {
		return NAME;
	}

	@Override
	public void initComponent() {
		controller = new IFMController(log);
		controller.setPaused(true);
		virtualFileActivityHandler = new VirtualFileActivityHandler(controller.getActivityHandler());

		initIfmController(IdeaFlowSettings.getInstance());

		ApplicationListener applicationListener = new ApplicationListener(controller);
		appConnection = ApplicationManager.getApplication().getMessageBus().connect();
		appConnection.subscribe(ApplicationActivationListener.TOPIC, applicationListener);
	}

	public void initIfmController(IdeaFlowSettings settingsStore) {
		String apiUrl = settingsStore.getApiUrl();
		String apiKey = settingsStore.getApiKey();
		if ((apiUrl == null) || (apiKey == null)) {
			log.info("Disabling Idea Flow Plugin controls because API-Key or URL is unavailable {ApiUrl=$apiUrl, ApiKey=$apiKey}. " +
					         "Please fix the plugin configuration in your IDE Preferences");
			return;
		}

		try {
			controller.initClients(apiUrl, apiKey);
		} catch (Exception ex) {
			// TODO: this should be a message popup to the user
			log.error("Failed to initialize controller: " + ex.getMessage());
		}

		IdeaFlowSettingsTaskManager taskManager = IdeaFlowSettings.getInstance().getTaskManager();
		List<TaskState> recentTasks = taskManager.getRecentTasks();
		// TODO: should probably record the active task in settings and set it to that...
		if (recentTasks.isEmpty() == false) {
			controller.setActiveTask(recentTasks.get(0));
		}
	}

	@Override
	public void disposeComponent() {
		controller.shutdown();
		appConnection.disconnect();
	}

	private static class ApplicationListener extends ApplicationActivationListener.Adapter {

		private DeactivationHandler deactivationHandler;

		ApplicationListener(IFMController controller) {
			deactivationHandler = new DeactivationHandler(controller);
		}

		@Override
		public void applicationActivated(IdeFrame ideFrame) {
			if (ideFrame.getProject() != null) {
				if (deactivationHandler.isPromptingForIdleTime() == false) {
					deactivationHandler.markActiveFileEventAsIdleIfDeactivationThresholdExceeded(ideFrame.getProject());
				}
			}
		}

		@Override
		public void applicationDeactivated(IdeFrame ideFrame) {
			if (ideFrame.getProject() != null) {
				deactivationHandler.deactivated();
			}
		}

	}

}
