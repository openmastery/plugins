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

	private static class ActivateIdeaFlowAction extends AnAction {

		private Project project
		private Task task

		public ActivateIdeaFlowAction(Project project, Task task) {
			this.project = project
			this.task = task

			getTemplatePresentation().setText(task.name, false)
			getTemplatePresentation().setDescription("Set ${task.name} as Active IdeaFlow")
		}

		public void actionPerformed(final AnActionEvent e) {
			IdeaFlowApplicationComponent.getIFMController().setActiveTask(task)
		}

	}

	private static class OpenActiveInVisualizerAction extends AnAction {

		private static final Icon BROWSE_ICON = IdeaFlowApplicationComponent.getIcon("browse.png")

		OpenActiveInVisualizerAction() {
			getTemplatePresentation().setText("Open in Visualizer")
			getTemplatePresentation().setDescription("Open the active IdeaFlow in the Visualizer")
			getTemplatePresentation().setIcon(BROWSE_ICON)
		}

		@Override
		void actionPerformed(AnActionEvent event) {
			IFMController controller = IdeaFlowApplicationComponent.getIFMController()
			Task task = controller.getActiveTask()

			if (task) {
				OpenInVisualizerAction.openTaskInBrowser(task)
			}
		}
	}

	private static class AddNewTaskAction extends AnAction {

		private Project project

		AddNewTaskAction(Project project) {
			this.project = project
			getTemplatePresentation().setText("Add new task...")
			getTemplatePresentation().setDescription("Add a new task")
		}

		@Override
		void actionPerformed(AnActionEvent e) {
			CreateTaskWizard wizard = new CreateTaskWizard(project)
			wizard.createTask()
		}
	}


	@Override
	protected DefaultActionGroup createPopupActionGroup(JComponent button) {
		DefaultActionGroup actionGroup = new DefaultActionGroup()
		Project project = PlatformDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext(button))

		if (project != null) {
			IFMController<Project> controller = IdeaFlowApplicationComponent.getIFMController()

			List<Task> recentTasks = controller.getRecentTasks()
			for (Task task : recentTasks) {
				if (task != controller.getActiveTask()) {
					actionGroup.add(new ActivateIdeaFlowAction(project, task))
				}
			}

			actionGroup.addSeparator();
			actionGroup.add(new OpenActiveInVisualizerAction())
			actionGroup.add(new AddNewTaskAction(project))
		}
		return actionGroup
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e)

		IFMController controller = getIFMController(e)
		if (controller) {
			e.presentation.enabled = controller.enabled
			e.presentation.text = controller.getActiveTaskName() ?: "Add new task"
		}
	}

}
