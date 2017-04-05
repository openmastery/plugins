package org.openmastery.ideaflow.intellij.action.event

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.openmastery.ideaflow.controller.IFMController
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent
import org.openmastery.ideaflow.intellij.action.ActionSupport


@Mixin(ActionSupport)
class FlushBatchEvent extends AnAction {

	@Override
	void actionPerformed(AnActionEvent anActionEvent) {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()
		controller.flushBatch()
	}

}
