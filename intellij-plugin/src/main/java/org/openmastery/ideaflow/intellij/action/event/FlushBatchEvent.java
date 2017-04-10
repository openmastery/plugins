package org.openmastery.ideaflow.intellij.action.event;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.openmastery.ideaflow.controller.IFMController;

import static org.openmastery.ideaflow.intellij.action.ActionSupport.getIFMController;

public class FlushBatchEvent extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent anActionEvent) {
		IFMController controller = getIFMController(anActionEvent);
		if (controller != null) {
			controller.flushBatch();
		}
	}

}
