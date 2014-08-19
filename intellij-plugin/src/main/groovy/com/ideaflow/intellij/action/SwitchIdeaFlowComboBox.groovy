package com.ideaflow.intellij.action

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.IdeaFlowApplicationComponent
import com.intellij.ide.BrowserUtil
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.actionSystem.ex.ComboBoxAction
import com.intellij.openapi.project.Project
import javax.swing.JComponent

@Mixin(ActionSupport)
class SwitchIdeaFlowComboBox extends ComboBoxAction {

	private static class ActivateIdeaFlowAction extends AnAction {
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
	}

	private static class OpenActiveInBrowserAction extends AnAction {

		OpenActiveInBrowserAction() {
			getTemplatePresentation().setText("Open Active in Browser")
			getTemplatePresentation().setDescription("Open the currently selected IdeaFlow map in preferred browser")
		}

		@Override
		void actionPerformed(AnActionEvent event) {
			IFMController controller = IdeaFlowApplicationComponent.getIFMController()
			File activeIfmFile = controller.activeIdeaFlowModel?.file

			if (activeIfmFile) {
				openInBrowser(activeIfmFile)
			}
		}

		public void openInBrowser(File file) {
			String ifmUrl = buildIfmUrl(file)
			BrowserUtil.open(ifmUrl)
		}

		// TODO: centralize this
		private String buildIfmUrl(File file) {
			return "http://localhost:8989/visualizer/ifm/open?filePath=${file.path}"
		}
	}

	private static class CloseActivateIdeaFlowAction extends AnAction {
		private Project project

		public CloseActivateIdeaFlowAction(Project project) {
			this.project = project
			getTemplatePresentation().setText("Close Active")
			getTemplatePresentation().setDescription("Close Active IdeaFlow")
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
			File activeFile = controller.activeIdeaFlowModel.file

			for (File ifmFile : controller.getOpenIdeaFlowFiles()) {
				if (ifmFile != activeFile) {
					actionGroup.add(new ActivateIdeaFlowAction(project, ifmFile))
				}
			}

			actionGroup.addSeparator();
			actionGroup.add(new OpenActiveInBrowserAction())
			actionGroup.add(new CloseActivateIdeaFlowAction(project))
		}
		return actionGroup
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e)

		IFMController controller = getIFMController(e)
		if (controller) {
			e.presentation.text = controller.getActiveIdeaFlowName()
		}
	}

}
