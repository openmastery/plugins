package com.ideaflow.intellij.action

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.IdeaFlowApplicationComponent
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.actionSystem.ex.ComboBoxAction
import com.intellij.openapi.project.Project
import javax.swing.Icon
import javax.swing.JComponent

/**
 * NOTE: all events generated from dynamically created actions seem to have the most recently opened project attached.
 * So, if multiple projects are opened, the activate/open/close actions invoked in one project could refer to the
 * other project when the event is processed.  Sucks but understandable since actions are generallly meant to be
 * instantiated via plugin.xml, not dynamically.  As a workaround, pass the project to the actions instead of
 * relying on the project associated with the event.
 * This applies to all the static inner classes but not to the ComboBoxAction itself since it's created in plugin.xml
 */
@Mixin(ActionSupport)
class SwitchIdeaFlowComboBox extends ComboBoxAction {

	private static class ActivateIdeaFlowAction extends AnAction {

		private static final Icon ACTIVE_ICON = IdeaFlowApplicationComponent.getIcon("ideaflow.png")
		private static final Icon INACTIVE_ICON = IdeaFlowApplicationComponent.getIcon("inactive.png")

		private Project project
		private File ifmFile

		public ActivateIdeaFlowAction(Project project, File ifmFile) {
			this.project = project
			this.ifmFile = ifmFile

			getTemplatePresentation().setText(ifmFile.name, false)
			getTemplatePresentation().setDescription("Activate ${ifmFile.name}")
		}

		public void actionPerformed(final AnActionEvent e) {
			IdeaFlowApplicationComponent.getIFMController().newIdeaFlow(project, ifmFile)
		}

		@Override
		void update(AnActionEvent e) {
			super.update(e)

			IFMController controller = IdeaFlowApplicationComponent.getIFMController()
			File activeIfmFile = controller.activeIdeaFlowModel?.file
			e.presentation.icon = (activeIfmFile == ifmFile) ? ACTIVE_ICON : INACTIVE_ICON
		}
	}

	private static class OpenActiveInBrowserAction extends AnAction {

		private static final Icon BROWSE_ICON = IdeaFlowApplicationComponent.getIcon("browse.png")

		OpenActiveInBrowserAction() {
			getTemplatePresentation().setText("Open Visualizer")
			getTemplatePresentation().setDescription("Open the currently selected IdeaFlow map in preferred browser")
			getTemplatePresentation().setIcon(BROWSE_ICON)
		}

		@Override
		void actionPerformed(AnActionEvent event) {
			IFMController controller = IdeaFlowApplicationComponent.getIFMController()
			File activeIfmFile = controller.activeIdeaFlowModel?.file

			if (activeIfmFile) {
				OpenInBrowserAction.openInBrowser(activeIfmFile)
			}
		}
	}

	private static class RemoveIdeaFlowAction extends AnAction {

		private static final Icon REMOVE_IDEAFLOW_ICON = IdeaFlowApplicationComponent.getIcon("ideaflow_remove.png")

		private Project project

		public RemoveIdeaFlowAction(Project project) {
			this.project = project
			getTemplatePresentation().setText("Remove")
			getTemplatePresentation().setDescription("Remove Active IdeaFlow")
			getTemplatePresentation().setIcon(REMOVE_IDEAFLOW_ICON)
		}

		public void actionPerformed(final AnActionEvent e) {
			IdeaFlowApplicationComponent.getIFMController().closeIdeaFlow(project)
		}
	}


	@Override
	protected DefaultActionGroup createPopupActionGroup(JComponent button) {
		DefaultActionGroup actionGroup = new DefaultActionGroup()
		Project project = PlatformDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext(button))

		if (project != null) {
			IFMController<Project> controller = IdeaFlowApplicationComponent.getIFMController()

			for (File ifmFile : controller.getOpenIdeaFlowFiles()) {
				actionGroup.add(new ActivateIdeaFlowAction(project, ifmFile))
			}

			actionGroup.addSeparator();
			actionGroup.add(new OpenActiveInBrowserAction())
			actionGroup.add(new RemoveIdeaFlowAction(project))
		}
		return actionGroup
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e)

		IFMController controller = getIFMController(e)
		if (controller) {
			boolean enabled = isIdeaFlowOpenAndNotPaused(e)
			e.presentation.enabled = enabled
			e.presentation.text = enabled ? controller.getActiveIdeaFlowName() : ""
		}
	}

}
