package com.ideaflow.intellij.action

import com.ideaflow.intellij.file.IdeaFlowMapFileType
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.vfs.VirtualFile

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

	private VirtualFile getSelectedIdeaFlowMapFile(AnActionEvent event) {
		VirtualFile[] selectedFiles = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(event.dataContext)

		selectedFiles.find { VirtualFile file ->
			isIdeaFlowMap(file)
		}
	}

	private boolean isIdeaFlowMap(VirtualFile file) {
		return IdeaFlowMapFileType.IFM_EXTENSION.equalsIgnoreCase(file.extension)
	}

	public void openInBrowser(VirtualFile file) {
		String ifmUrl = buildIfmUrl(file)
		BrowserUtil.open(ifmUrl)
	}

	private String buildIfmUrl(VirtualFile file) {
		return "http://localhost:8080/visualizer/ifm/open?filePath=${file.path}"
	}

}
