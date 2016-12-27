package org.openmastery.ideaflow.intellij.file

import com.ideaflow.activity.ActivityHandler
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class VirtualFileActivityHandler {

	private ActivityHandler activityHandler

	VirtualFileActivityHandler(ActivityHandler activityHandler) {
		this.activityHandler = activityHandler
	}

	void startFileEvent(Project project, VirtualFile file) {
		String filePath = getFilePath(project, file)
		activityHandler.startFileEvent(filePath)
	}

	void endFileEvent(Project project, VirtualFile file) {
		String filePath = getFilePath(project, file)
		activityHandler.endFileEvent(filePath)
	}

	void fileModified(Project project, VirtualFile file) {
		String filePath = getFilePath(project, file)
		activityHandler.fileModified(filePath)
	}

	private String getFilePath(Project project, VirtualFile file) {
		if (file == null) {
			return null
		}

		String filePath = file.name
		if (project != null) {
			try {
				String fullFilePath = getFullFilePathOrNull(file, project)
				if (fullFilePath != null) {
					filePath = fullFilePath
				}
			} catch (Exception ex) {
				// ignore any issue resolving full file path and just default to file name
			}
		}
		filePath
	}

	private String getFullFilePathOrNull(VirtualFile file, Project project) {
		String fullFilePath = file.path
		String filePath = null

		Module module = ModuleUtil.findModuleForFile(file, project)
		if (module != null) {
			String moduleBasePath = module.getModuleFile().getParent().path
			if (fullFilePath.startsWith(moduleBasePath)) {
				filePath = fullFilePath.substring(moduleBasePath.length())
			}
		} else {
			String projectBasePath = project.basePath
			if (fullFilePath.startsWith(projectBasePath)) {
				filePath = fullFilePath.substring(projectBasePath.length())
			}
		}
		filePath
	}

}
