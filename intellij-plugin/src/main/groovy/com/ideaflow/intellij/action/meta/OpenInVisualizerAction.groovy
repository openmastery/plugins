package com.ideaflow.intellij.action.meta

import com.ideaflow.intellij.action.ActionSupport
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.vfs.VirtualFile
import org.openmastery.publisher.api.task.Task

@Mixin(ActionSupport)
class OpenInVisualizerAction extends AnAction {

	@Override
	public void update(AnActionEvent event) {
		boolean enabled = getActiveTask(event) != null
		Presentation presentation = event.getPresentation()
		presentation.setEnabled(enabled)
	}

	@Override
	void actionPerformed(AnActionEvent event) {
		Task task = getActiveTask(event)
		if (task) {
			openTaskInBrowser(task)
		}
	}

	static void openTaskInBrowser(Task task) {
		if (task != null) {
			String ifmUrl = "http://localhost:8980/visualizer/#taskId=${task.id}"
			BrowserUtil.open(ifmUrl)
		}
	}

}
