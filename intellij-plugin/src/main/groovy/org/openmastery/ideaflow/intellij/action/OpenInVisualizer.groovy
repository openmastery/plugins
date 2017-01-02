package org.openmastery.ideaflow.intellij.action

import org.openmastery.ideaflow.state.TaskState
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import org.openmastery.ideaflow.intellij.action.ActionSupport

@Mixin(ActionSupport)
class OpenInVisualizer extends AnAction {

	@Override
	public void update(AnActionEvent event) {
		boolean enabled = getActiveTask(event) != null
		Presentation presentation = event.getPresentation()
		presentation.setEnabled(enabled)
	}

	@Override
	void actionPerformed(AnActionEvent event) {
		TaskState task = getActiveTask(event)
		if (task) {
			openTaskInBrowser(task)
		}
	}

	static void openTaskInBrowser(TaskState task) {
		if (task != null) {
			String ifmUrl = "http://localhost:8980/visualizer/#taskId=${task.id}"
			BrowserUtil.open(ifmUrl)
		}
	}

}
