package com.ideaflow.intellij.action.meta

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.action.ActionSupport
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.actionSystem.ex.ComboBoxAction
import com.intellij.openapi.project.Project
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent
import org.openmastery.ideaflow.intellij.settings.IdeaFlowSettings
import org.openmastery.ideaflow.intellij.settings.IdeaFlowSettingsTaskManager
import org.openmastery.publisher.api.task.Task

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
			//TODO get recent tasks for project, and active task for project

			IFMController controller = IdeaFlowApplicationComponent.getIFMController()
			for (Task task : taskManager.recentTasks) {
				if (task != controller.activeTask) {
					actionGroup.add(new ActivateIdeaFlowAction(this, project, task))
				}
			}

			actionGroup.addSeparator();
			actionGroup.add(new OpenActiveInVisualizerAction(this, ))
			actionGroup.add(new AddNewTaskAction(this, project))
		}
		return actionGroup
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e)

		IFMController controller = getIFMController(e)
		if (controller) {
			e.presentation.text = controller.activeTaskName ?: "Add new task"
		}
	}

	void activateTask(Task task) {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()
		controller.activeTask = task
		controller.paused = false
	}

	void addNewTask(String name, String description) {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()
		Task task = controller.newTask(name, description)
		controller.activeTask = task
		controller.paused = false
		taskManager.addRecentTask(task);
	}

	void openActiveTaskInVisualizer() {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()
		Task task = controller.getActiveTask()

		if (task) {
			OpenInVisualizerAction.openTaskInBrowser(task)
		}
	}

	private IdeaFlowSettingsTaskManager getTaskManager() {
		IdeaFlowSettings.instance.taskManager
	}



	private static class ActivateIdeaFlowAction extends AnAction {

		private SwitchIdeaFlowComboBox switchIdeaFlow
		private Project project
		private Task task

		public ActivateIdeaFlowAction(SwitchIdeaFlowComboBox switchIdeaFlow, Project project, Task task) {
			this.switchIdeaFlow = switchIdeaFlow
			this.project = project
			this.task = task

			getTemplatePresentation().setText(task.name, false)
			getTemplatePresentation().setDescription("Set ${task.name} as Active IdeaFlow")
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
					switchIdeaFlow.addNewTask(wizard.taskName, wizard.taskDescription)
				} catch (Exception ex) {
					IdeaFlowApplicationComponent.showErrorMessage("Unable to connect to server",
							"Sorry, the server is currently unavailable for creating new tasks.  " +
									"You can currently work offline only with existing tasks.  " +
									"If the lack of connectivity is unexpected, please make sure the URL and API-Key" +
								" are configured correctly in Idea Flow Preferences.  Server Error: "+	ex.message)
				}

			}
		}
	}
}
