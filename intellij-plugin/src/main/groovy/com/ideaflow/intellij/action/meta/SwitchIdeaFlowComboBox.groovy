package com.ideaflow.intellij.action.meta

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.action.ActionSupport
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.ex.ComboBoxAction
import com.intellij.openapi.project.Project
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent
import org.openmastery.ideaflow.intellij.settings.IdeaFlowSettings
import org.openmastery.ideaflow.intellij.settings.IdeaFlowSettingsTaskManager
import com.ideaflow.state.TaskState

import javax.swing.Icon
import javax.swing.JComponent

/**
 * NOTE: all events generated from dynamically created actions seem to have the most recently opened project attached.
 * So, if multiple projects are opened, the activate/open/close actions invoked in one project could refer to the
 * other project when the event is processed.  Sucks but understandable since actions are generally meant to be
 * instantiated via plugin.xml, not dynamically.  As a workaround, pass the project to the actions instead of
 * relying on the project associated with the event.
 * This applies to all the static inner classes but not to the ComboBoxAction itself since it's created in plugin.xml
 */
@Mixin(ActionSupport)
class SwitchIdeaFlowComboBox extends ComboBoxAction {

	@Override
	protected DefaultActionGroup createPopupActionGroup(JComponent button) {
		DefaultActionGroup actionGroup = new DefaultActionGroup()
		Project project = PlatformDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext(button))

		if (project != null) {
			IFMController controller = IdeaFlowApplicationComponent.getIFMController()
			for (TaskState task : taskManager.recentTasks) {
				if (task != controller.activeTask) {
					actionGroup.add(new ActivateIdeaFlowAction(this, project, task))
				}
			}

			actionGroup.addSeparator();
			if (controller.hasActiveTask()) {
				actionGroup.add(new OpenActiveInVisualizerAction(this))
				actionGroup.add(new CloseActiveIdeaFlowAction(this))
			}
			actionGroup.add(new AddNewTaskAction(this, project))
		}
		return actionGroup
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e)
		IFMController controller = getIFMController(e)
		if (controller) {
			TaskState activeTask = controller.activeTask
			if (activeTask != null) {
				e.presentation.text = activeTask.name
				e.presentation.description = activeTask.description
			} else {
				e.presentation.text = "Add new task"
				e.presentation.description = ""
			}
		}
	}


	void activateTask(TaskState task) {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()
		controller.activateTask(task)
	}

	void addNewTask(String name, String description, String project) {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()
		TaskState task = controller.createAndActivateTask(name, description, project)
		taskManager.addRecentTask(task);
	}

	void openActiveTaskInVisualizer() {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()
		TaskState task = controller.getActiveTask()

		if (task) {
			OpenInVisualizerAction.openTaskInBrowser(task)
		}
	}

	void closeActiveTask() {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()
		TaskState activeTask = controller.clearActiveTask()

		if (activeTask != null) {
			taskManager.removeTask(activeTask)
		}
	}

	private IdeaFlowSettingsTaskManager getTaskManager() {
		IdeaFlowSettings.instance.taskManager
	}


	private static class ActivateIdeaFlowAction extends AnAction {

		private SwitchIdeaFlowComboBox switchIdeaFlow
		private Project project
		private TaskState task

		public ActivateIdeaFlowAction(SwitchIdeaFlowComboBox switchIdeaFlow, Project project, TaskState task) {
			this.switchIdeaFlow = switchIdeaFlow
			this.project = project
			this.task = task

			getTemplatePresentation().setText(task.name, false)
			getTemplatePresentation().setDescription("Set ${task.name} as active IdeaFlow")
		}

		public void actionPerformed(final AnActionEvent e) {
			switchIdeaFlow.activateTask(task)
		}

	}

	private static class OpenActiveInVisualizerAction extends AnAction {

		private static final Icon BROWSE_ICON = IdeaFlowApplicationComponent.getIcon("browse.png")

		private SwitchIdeaFlowComboBox switchIdeaFlow

		OpenActiveInVisualizerAction(SwitchIdeaFlowComboBox switchIdeaFlow) {
			this.switchIdeaFlow = switchIdeaFlow
			getTemplatePresentation().setText("Open in Visualizer")
			getTemplatePresentation().setDescription("Open the active IdeaFlow in the Visualizer")
			getTemplatePresentation().setIcon(BROWSE_ICON)
		}

		@Override
		void actionPerformed(AnActionEvent event) {
			switchIdeaFlow.openActiveTaskInVisualizer()
		}
	}

	private static class CloseActiveIdeaFlowAction extends AnAction {

		private SwitchIdeaFlowComboBox switchIdeaFlow

		public CloseActiveIdeaFlowAction(SwitchIdeaFlowComboBox switchIdeaFlow) {
			this.switchIdeaFlow = switchIdeaFlow

			getTemplatePresentation().setText("Close active task")
			getTemplatePresentation().setDescription("Close the active task")
		}

		public void actionPerformed(final AnActionEvent e) {
			switchIdeaFlow.closeActiveTask()
		}

	}

	private static class AddNewTaskAction extends AnAction {

		private SwitchIdeaFlowComboBox switchIdeaFlow
		private Project project

		AddNewTaskAction(SwitchIdeaFlowComboBox switchIdeaFlow, Project project) {
			this.switchIdeaFlow = switchIdeaFlow
			this.project = project
			getTemplatePresentation().setText("Add new task...")
			getTemplatePresentation().setDescription("Add a new task")

		}

		@Override
		void actionPerformed(AnActionEvent e) {
			CreateTaskWizard wizard = new CreateTaskWizard(project)
			if (wizard.shouldCreateTask()) {
				try {
					switchIdeaFlow.addNewTask(wizard.taskName, wizard.taskDescription, wizard.taskProject)
				} catch (Exception ex) {
					String message = "Sorry, the server is currently unavailable for creating new tasks.  " +
							"You can currently work offline only with existing tasks.  " +
							"If the lack of connectivity is unexpected, please make sure the URL and API-Key" +
							" are configured correctly in Idea Flow Preferences.  Server Error: " + ex.message
					IdeaFlowApplicationComponent.showErrorMessage("Unable to connect to server", message)
				}

			}
		}
	}
}
