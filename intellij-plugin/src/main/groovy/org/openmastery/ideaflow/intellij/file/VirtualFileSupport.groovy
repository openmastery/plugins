package org.openmastery.ideaflow.intellij.file

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class VirtualFileSupport {

	String getFilePath(Project project, VirtualFile file) {
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

	VirtualFile getActiveFileSelection(Project project) {
		VirtualFile file = null
		FileEditorManager fileEditorManager = FileEditorManager.getInstance(project)
		if (fileEditorManager != null) {
			VirtualFile[] files = fileEditorManager.getSelectedFiles()
			if (files.length > 0) {
				file = files[0]
			}
		}
		return file
	}

}
