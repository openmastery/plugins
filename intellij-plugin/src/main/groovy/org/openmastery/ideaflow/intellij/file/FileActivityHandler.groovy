package org.openmastery.ideaflow.intellij.file

import com.ideaflow.controller.IFMController
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.joda.time.DateTime
import org.joda.time.Duration
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent


class FileActivityHandler {

	private static final int SHORTEST_ACTIVITY = 3

	private VirtualFileSupport fileSupport = new VirtualFileSupport()
	private FileActivity activeFileActivity = null

	private IFMController getController() {
		IdeaFlowApplicationComponent.getIFMController()
	}

	private boolean isSame(String newFilePath) {
		isDifferent(newFilePath) == false
	}

	private boolean isDifferent(String newFilePath) {
		if (activeFileActivity == null) {
			return newFilePath != null
		} else {
			return activeFileActivity.filePath != newFilePath
		}
	}

	private boolean isOverActivityThreshold() {
		activeFileActivity != null && activeFileActivity.durationInSeconds >= SHORTEST_ACTIVITY
	}

	void markIdleTime(Duration idleDuration) {
		String lastActivityDuration = null
		if (activeFileActivity != null) {
			lastActivityDuration = "${activeFileActivity.durationInSeconds - idleDuration.standardSeconds}s"
		}
		println "markIdleTime, idle=${idleDuration}, lastFileActivityDuration=${lastActivityDuration}"
	}

	void markExternalActivity(Duration idleDuration) {
		println "markExternalActivity  ${idleDuration}"
		println "activeFileActivity = ${activeFileActivity}"
		if (idleDuration.standardSeconds >= SHORTEST_ACTIVITY) {
			String lastActivityDuration = null
			if (activeFileActivity != null) {
				lastActivityDuration = "${activeFileActivity.durationInSeconds - idleDuration.standardSeconds}s"
			}
			println "markExternalActivity, duration=${idleDuration}, lastFileActivityDuration=${lastActivityDuration}"
		}
	}


	void startFileEvent(Project project, VirtualFile file) {
		String filePath = fileSupport.getFilePath(project, file)
		if (isDifferent(filePath)) {
			if (isOverActivityThreshold()) {
				println "saveFileActivity ${activeFileActivity}"
			}

			activeFileActivity = createFileActivity(filePath)
		}
		println "activeFileActivity = ${activeFileActivity}"
	}

//	void startFileEventForCurrentFile(Project project) {
//		VirtualFile activeFile = fileSupport.getActiveFileSelection(project)
//		startFileEvent(project, activeFile)
//	}

	void endFileEvent(Project project, VirtualFile file) {
		String filePath = fileSupport.getFilePath(project, file)
		if (isSame(filePath)) {
			startFileEvent(project, null)
		}
	}

	void fileModified(Project project, VirtualFile file) {
		String filePath = fileSupport.getFilePath(project, file)
		println "fileModified ${filePath}"
		if (activeFileActivity?.filePath == filePath) {
			activeFileActivity.modified = true
		}
	}

//	void markActiveFileEventAsIdle(String comment) {
//		println "mark active file as idle ${comment}"
////		eventToIntervalHandler?.endActiveEventAsIdle(comment)
//	}

	private FileActivity createFileActivity(filePath) {
		filePath == null ? null : new FileActivity(filePath: filePath, time: DateTime.now(), modified: false)
	}

	private static class FileActivity {
		DateTime time
		String filePath
		boolean modified

		public long getDurationInSeconds() {
			new Duration(time, DateTime.now()).standardSeconds
		}

		public String toString() {
			"FileActivity [path=${filePath}, modified=${modified}, duration=${durationInSeconds}]"
		}
	}

}
