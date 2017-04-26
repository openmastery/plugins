package org.openmastery.ideaflow.intellij.action;

import com.bancvue.rest.exception.ConflictException;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.project.Project;
import org.openmastery.ideaflow.controller.IFMController;
import org.openmastery.ideaflow.controller.NoSuchTaskToResumeException;
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent;
import org.openmastery.ideaflow.intellij.action.wizard.CreateTaskWizard;
import org.openmastery.ideaflow.intellij.action.wizard.ResumeTaskWizard;
import org.openmastery.ideaflow.state.TaskState;

import javax.swing.Icon;
import javax.swing.JComponent;

import static org.openmastery.ideaflow.intellij.action.ActionSupport.getActiveTask;
import static org.openmastery.ideaflow.intellij.action.ActionSupport.getTaskManager;

/**
 * NOTE: all events generated from dynamically created actions seem to have the most recently opened project attached.
 * So, if multiple projects are opened, the activate/open/close actions invoked in one project could refer to the
 * other project when the event is processed.  Sucks but understandable since actions are generally meant to be
 * instantiated via plugin.xml, not dynamically.  As a workaround, pass the project to the actions instead of
 * relying on the project associated with the event.
 * This applies to all the static inner classes but not to the ComboBoxAction itself since it's created in plugin.xml
 */
public class SwitchIdeaFlowComboBox extends ComboBoxAction {

	@Override
	protected DefaultActionGroup createPopupActionGroup(JComponent button) {
		DefaultActionGroup actionGroup = new DefaultActionGroup();
		Project project = PlatformDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext(button));

		if (project != null) {
			IFMController controller = IdeaFlowApplicationComponent.getIFMController();
			for (TaskState task : getTaskManager().getRecentTasks()) {
				if (task != controller.getActiveTask()) {
					actionGroup.add(new ActivateIdeaFlowAction(this, task));
				}
			}

			actionGroup.addSeparator();
			if (controller.hasActiveTask()) {
				actionGroup.add(new OpenActiveInVisualizerAction(this));
				actionGroup.add(new CloseActiveIdeaFlowAction(this));
			}
			actionGroup.add(new AddNewTaskAction(this, project));
			actionGroup.add(new ResumeExistingTaskAction(this, project));
		}
		return actionGroup;
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);

		TaskState activeTask = getActiveTask(e);
		String text, description;
		if (activeTask != null) {
			text = activeTask.getQualifiedName();
			description = activeTask.getDescription();
		} else {
			text = "Add new task";
			description = "";
		}
		e.getPresentation().setText(text);
		e.getPresentation().setDescription(description);
	}


	public void activateTask(TaskState task) {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController();
		controller.activateTask(task);
	}

	public void addNewTask(String name, String description, String project) {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController();
		TaskState task = controller.createAndActivateTask(name, description, project);
		getTaskManager().addRecentTask(task);
	}

	public void resumeExistingTask(String name) throws NoSuchTaskToResumeException {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController();
		TaskState task = controller.resumeAndActivateTask(name);
		getTaskManager().addRecentTask(task);
	}

	public void openActiveTaskInVisualizer() {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController();
		TaskState task = controller.getActiveTask();

		if (task != null) {
			OpenInVisualizer.openTaskInBrowser(task);
		}
	}

	public void closeActiveTask() {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController();
		TaskState activeTask = controller.clearActiveTask();

		if (activeTask != null) {
			getTaskManager().removeTask(activeTask);
		}
	}


	private static class ActivateIdeaFlowAction extends AnAction {

		private SwitchIdeaFlowComboBox switchIdeaFlow;
		private TaskState task;

		public ActivateIdeaFlowAction(SwitchIdeaFlowComboBox switchIdeaFlow, TaskState task) {
			this.switchIdeaFlow = switchIdeaFlow;
			this.task = task;

			getTemplatePresentation().setText(task.getQualifiedName(), false);
			getTemplatePresentation().setDescription("Set " + task.getName() + " as active IdeaFlow");
		}

		public void actionPerformed(final AnActionEvent e) {
			switchIdeaFlow.activateTask(task);
		}

	}

	private static class OpenActiveInVisualizerAction extends AnAction {

		private static final Icon BROWSE_ICON = IdeaFlowApplicationComponent.getIcon("browse.png");

		private SwitchIdeaFlowComboBox switchIdeaFlow;

		OpenActiveInVisualizerAction(SwitchIdeaFlowComboBox switchIdeaFlow) {
			this.switchIdeaFlow = switchIdeaFlow;
			getTemplatePresentation().setText("Open in Visualizer");
			getTemplatePresentation().setDescription("Open the active IdeaFlow in the Visualizer");
			getTemplatePresentation().setIcon(BROWSE_ICON);
		}

		@Override
		public void actionPerformed(AnActionEvent event) {
			switchIdeaFlow.openActiveTaskInVisualizer();
		}
	}

	private static class CloseActiveIdeaFlowAction extends AnAction {

		private SwitchIdeaFlowComboBox switchIdeaFlow;

		public CloseActiveIdeaFlowAction(SwitchIdeaFlowComboBox switchIdeaFlow) {
			this.switchIdeaFlow = switchIdeaFlow;

			getTemplatePresentation().setText("Close active task");
			getTemplatePresentation().setDescription("Close the active task");
		}

		public void actionPerformed(final AnActionEvent e) {
			switchIdeaFlow.closeActiveTask();
		}

	}

	private static class AddNewTaskAction extends AnAction {

		private SwitchIdeaFlowComboBox switchIdeaFlow;
		private Project project;

		AddNewTaskAction(SwitchIdeaFlowComboBox switchIdeaFlow, Project project) {
			this.switchIdeaFlow = switchIdeaFlow;
			this.project = project;
			getTemplatePresentation().setText("Add new task...");
			getTemplatePresentation().setDescription("Add a new task");

		}

		@Override
		public void actionPerformed(AnActionEvent e) {
			CreateTaskWizard wizard = new CreateTaskWizard(project);
			if (wizard.shouldCreateTask()) {
				try {
					switchIdeaFlow.addNewTask(wizard.getTaskName(), wizard.getTaskDescription(), wizard.getTaskProject());
				} catch (ConflictException ex) {
					String message = "Task with name '" + wizard.getTaskName() + "' already exists";
					IdeaFlowApplicationComponent.showErrorMessage("Duplicate task", message);
				} catch (Exception ex) {
					String message = "Sorry, the server is currently unavailable for creating new tasks.  " +
							"You can currently work offline only with existing tasks.  " +
							"If the lack of connectivity is unexpected, please make sure the URL and API-Key " +
							"are configured correctly in Idea Flow Preferences.  Server Error: " + ex.getMessage();
					IdeaFlowApplicationComponent.showErrorMessage("Unable to connect to server", message);
				}

			}
		}
	}

	private static class ResumeExistingTaskAction extends AnAction {

		private SwitchIdeaFlowComboBox switchIdeaFlow;
		private Project project;

		ResumeExistingTaskAction(SwitchIdeaFlowComboBox switchIdeaFlow, Project project) {
			this.switchIdeaFlow = switchIdeaFlow;
			this.project = project;
			getTemplatePresentation().setText("Resume task...");
			getTemplatePresentation().setDescription("Resume a previously created task");
		}

		@Override
		public void actionPerformed(AnActionEvent e) {
			ResumeTaskWizard wizard = new ResumeTaskWizard(project);
			if (wizard.shouldCreateTask()) {
				try {
					switchIdeaFlow.resumeExistingTask(wizard.getTaskName());
				} catch (NoSuchTaskToResumeException ex) {
					IdeaFlowApplicationComponent.showErrorMessage("Unable to resume task", ex.getMessage());
				} catch (Exception ex) {
					String message = "Sorry, the server is currently unavailable for creating new tasks.  " +
							"You can currently work offline only with existing tasks.  " +
							"If the lack of connectivity is unexpected, please make sure the URL and API-Key " +
							"are configured correctly in Idea Flow Preferences.  Server Error: " + ex.getMessage();
					IdeaFlowApplicationComponent.showErrorMessage("Unable to connect to server", message);
				}

			}
		}
	}

}
