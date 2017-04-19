package org.openmastery.ideaflow.intellij.action;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import org.openmastery.ideaflow.state.TaskState;

import static org.openmastery.ideaflow.intellij.action.ActionSupport.getActiveTask;

public class OpenInVisualizer extends AnAction {

	@Override
	public void update(AnActionEvent event) {
		boolean enabled = getActiveTask(event) != null;
		Presentation presentation = event.getPresentation();
		presentation.setEnabled(enabled);
	}

	@Override
	public void actionPerformed(AnActionEvent event) {
		TaskState task = getActiveTask(event);
		if (task != null) {
			openTaskInBrowser(task);
		}
	}

	static void openTaskInBrowser(TaskState task) {
		if (task != null) {
			String ifmUrl = "http://localhost:8980/visualizer/#taskId=" + task.getId();
			BrowserUtil.open(ifmUrl);
		}
	}

}
