package com.ideaflow.intellij

import com.ideaflow.controller.IDEService
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class IDEServiceImpl implements IDEService<Project> {

	@Override
	String getActiveFileSelection(Project project) {
		String file = null
		FileEditorManager fileEditorManager = FileEditorManager.getInstance(project)
		if (fileEditorManager != null) {
			VirtualFile[] files = fileEditorManager.getSelectedFiles()
			if (files.length > 0) {
				file = files[0].name
			}
		}
		return file
	}

}
