package com.ideaflow.intellij.action

import com.ideaflow.controller.IFMController
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

@Mixin(ActionSupport)
class ActivateIdeaFlowAction extends AnAction {

	private IdeaFlowCreator creator = new IdeaFlowCreator()

	@Override
	void actionPerformed(AnActionEvent e) {
		IFMController controller = getIFMController(e)

		if (controller != null) {
			creator.createNewIdeaFlow(e)
		}
	}

}

