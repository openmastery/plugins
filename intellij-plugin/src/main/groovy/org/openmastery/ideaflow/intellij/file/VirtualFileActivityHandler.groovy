package org.openmastery.ideaflow.intellij.file

import com.ideaflow.activity.ActivityHandler
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.joda.time.Duration

class VirtualFileActivityHandler {

	private ActivityHandler activityHandler

	VirtualFileActivityHandler(ActivityHandler activityHandler) {
		this.activityHandler = activityHandler
	}

	void markIdleTime(Duration idleDuration) {
		activityHandler.markIdleTime(idleDuration)
	}

	void markExternalActivity(Duration idleDuration) {
		activityHandler.markExternalActivity(idleDuration)
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

}
