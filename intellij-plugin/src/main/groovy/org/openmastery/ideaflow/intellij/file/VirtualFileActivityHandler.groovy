package org.openmastery.ideaflow.intellij.file

import com.ideaflow.activity.FileActivityHandler
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.joda.time.Duration

class VirtualFileActivityHandler {

	private FileActivityHandler fileActivityHandler

	VirtualFileActivityHandler(FileActivityHandler fileActivityHandler) {
		this.fileActivityHandler = fileActivityHandler
	}

	void markIdleTime(Duration idleDuration) {
		fileActivityHandler.markIdleTime(idleDuration)
	}

	void markExternalActivity(Duration idleDuration) {
		fileActivityHandler.markExternalActivity(idleDuration)
	}

	void startFileEvent(Project project, VirtualFile file) {
		String filePath = getFilePath(project, file)
		fileActivityHandler.startFileEvent(filePath)
	}

	void endFileEvent(Project project, VirtualFile file) {
		String filePath = getFilePath(project, file)
		fileActivityHandler.endFileEvent(filePath)
	}

	void fileModified(Project project, VirtualFile file) {
		String filePath = getFilePath(project, file)
		fileActivityHandler.fileModified(filePath)
	}

	private String getFilePath(Project project, VirtualFile file) {
		if (file == null) {
			return null
		}

		String filePath = file.name
		if (project != null) {
			String projectBasePath = project.basePath
			String fullFilePath = file.path

			// TODO: detect path via module
//			Module module = ModuleUtil.findModuleForFile(file, project)

			if (fullFilePath.startsWith(projectBasePath)) {
				filePath = fullFilePath.substring(projectBasePath.length())
			}
		}
		filePath
	}

//	VirtualFile getActiveFileSelection(Project project) {
//		VirtualFile file = null
//		FileEditorManager fileEditorManager = FileEditorManager.getInstance(project)
//		if (fileEditorManager != null) {
//			VirtualFile[] files = fileEditorManager.getSelectedFiles()
//			if (files.length > 0) {
//				file = files[0]
//			}
//		}
//		return file
//	}

}
