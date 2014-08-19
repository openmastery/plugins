package com.ideaflow.intellij.action

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.vfs.VirtualFile

@Mixin(ActionSupport)
class OpenInBrowserAction extends AnAction {

	@Override
	public void update(AnActionEvent event) {
		boolean enabled = getSelectedIdeaFlowMapFile(event) != null

		Presentation presentation = event.getPresentation()
		presentation.setEnabled(enabled)
	}

	@Override
	void actionPerformed(AnActionEvent event) {
		VirtualFile file = getSelectedIdeaFlowMapFile(event);

		if (file) {
			openInBrowser(file)
		}
	}

	public static void openInBrowser(VirtualFile file) {
		openPathInBrowser(file.path)
	}

	public static void openInBrowser(File file) {
		openPathInBrowser(file.absolutePath)
	}

	private static void openPathInBrowser(String path) {
		String ifmUrl = "http://localhost:8989/visualizer/ifm/open?filePath=${path}"
		BrowserUtil.open(ifmUrl)
	}

}
