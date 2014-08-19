package com.ideaflow.intellij.action

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.IdeaFlowApplicationComponent
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.GlobalSearchScope
import javax.swing.Icon

@Mixin(ActionSupport)
class ToggleOpenIdeaFlow extends IdeaFlowToggleAction {

	private static final String OPEN_TITLE = "Open IdeaFlow"
	private static final String CLOSE_TITLE = "Close IdeaFlow"

	private IdeaFlowCreator ideaFlowCreator = new IdeaFlowCreator()

	@Override
	protected boolean isPresentationEnabled(AnActionEvent e) {
		return true
	}

	@Override
	protected String getPresentationText(AnActionEvent e) {
		return isIdeaFlowOpen(e) ? CLOSE_TITLE : OPEN_TITLE
	}

	@Override
	protected String getPresentationDescription(AnActionEvent e) {
		return "${getPresentationText(e)}: ${getActiveIdeaFlowName(e)}"
	}

	@Override
	boolean isSelected(AnActionEvent e) {
		return isIdeaFlowOpen(e)
	}

	@Override
	void setSelected(AnActionEvent e, boolean state) {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()

		if (controller.isIdeaFlowOpen()) {
			controller.closeIdeaFlow(e.project)
		} else {
			ideaFlowCreator.createNewIdeaFlow(e)
		}
	}

}