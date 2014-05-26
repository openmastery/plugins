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

	public void openInBrowser(VirtualFile file) {
		String ifmUrl = buildIfmUrl(file)
		BrowserUtil.open(ifmUrl)
	}

	private String buildIfmUrl(VirtualFile file) {
		return "http://localhost:8080/visualizer/ifm/open?filePath=${file.path}"
	}

}
